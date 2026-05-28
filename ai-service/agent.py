"""LangGraph 智能客服 Agent"""
import os, sys
print("[AI服务] 加载模块...", flush=True)

# 阻止tiktoken在import时联网下载分词器（国内网络卡死的第一元凶）
os.environ.setdefault("TIKTOKEN_CACHE_DIR", os.path.join(os.path.dirname(__file__), ".tiktoken"))
os.environ.setdefault("OPENAI_LOG", "info")

import json
from dotenv import load_dotenv
load_dotenv()

print("[AI服务] 导入langchain...", flush=True)
from langgraph.graph import StateGraph, END
from langgraph.prebuilt import ToolNode
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.tools import tool
from langchain_openai import ChatOpenAI
print("[AI服务] langchain导入完成", flush=True)

from tools import search_jobs, get_job_detail, recommend_jobs

SYSTEM_PROMPT = """你是"日结兼职平台"的智能客服助手，名叫小兼。

你的职责：
1. 帮助用户查找日结兼职岗位。用户可能描述需求如"朝阳区餐饮类的日结"、"薪资200以上的快递分拣"。
2. 推荐适合用户的岗位。
3. 回答关于平台使用的问题（如何报名、如何签到等）。

规则：
- 始终用中文回复，语气友好热情。
- 当用户询问找工作时，调用相应工具获取实时数据，然后整理成易读的格式回复。
- 回复岗位时列出：岗位名称、公司地址、工作时间、日薪、要求。
- 如果搜索结果为空，告诉用户当前没有匹配的岗位，建议放宽条件。
- 不要编造数据，只基于工具返回的真实数据回答。
- 用户询问无关话题时，礼貌引导回找工作的话题。"""

_current_token: str = ""
_agent = None


def _get_current_token() -> str:
    return _current_token


def _build_agent():
    print("[AI服务] 正在初始化LLM...", flush=True)
    llm = ChatOpenAI(
        model=os.getenv("LLM_MODEL", "deepseek-chat"),
        api_key=os.getenv("LLM_API_KEY"),
        base_url=os.getenv("LLM_BASE_URL", "https://api.deepseek.com"),
        temperature=0.3,
        max_retries=1,
        timeout=30,
    )
    print("[AI服务] LLM就绪，构建Agent...", flush=True)

    @tool
    def tool_search_jobs(keyword: str = "", job_type: str = "",
                         min_salary: int = 0, max_salary: int = 0,
                         work_address: str = "") -> str:
        """搜索日结兼职岗位。"""
        result = search_jobs(_get_current_token(), keyword=keyword, job_type=job_type,
                             min_salary=min_salary, max_salary=max_salary,
                             work_address=work_address, page_size=5)
        return json.dumps(result, ensure_ascii=False, indent=2)

    @tool
    def tool_get_job_detail(job_id: int) -> str:
        """查询单个岗位的完整详情。"""
        result = get_job_detail(_get_current_token(), job_id)
        return json.dumps(result, ensure_ascii=False, indent=2)

    @tool
    def tool_recommend_jobs() -> str:
        """推荐最匹配的日结岗位。"""
        result = recommend_jobs(_get_current_token())
        return json.dumps(result, ensure_ascii=False, indent=2)

    tools = [tool_search_jobs, tool_get_job_detail, tool_recommend_jobs]
    llm_with_tools = llm.bind_tools(tools)

    def chatbot(state):
        messages = [SystemMessage(content=SYSTEM_PROMPT)] + state["messages"]
        response = llm_with_tools.invoke(messages)
        return {"messages": [response], "token": state["token"]}

    def route_tools(state):
        last_msg = state["messages"][-1]
        if hasattr(last_msg, "tool_calls") and last_msg.tool_calls:
            return "tools"
        return END

    graph = StateGraph(dict)
    graph.add_node("chatbot", chatbot)
    graph.add_node("tools", ToolNode(tools))
    graph.set_entry_point("chatbot")
    graph.add_conditional_edges("chatbot", route_tools, {"tools": "tools", END: END})
    graph.add_edge("tools", "chatbot")

    return graph.compile()


def chat(message: str, token: str, history: list = None) -> str:
    global _current_token, _agent
    _current_token = token

    if _agent is None:
        _agent = _build_agent()
        print("[AI服务] Agent就绪", flush=True)

    messages = []
    if history:
        for h in history[-10:]:  # 只保留最近10轮，避免token超限
            if h.get("role") == "user":
                messages.append(HumanMessage(content=h["content"]))
            elif h.get("role") == "assistant":
                messages.append(AIMessage(content=h["content"]))

    messages.append(HumanMessage(content=message))

    try:
        result = _agent.invoke({"messages": messages, "token": token})
        return result["messages"][-1].content
    except Exception as e:
        print(f"[AI服务] Agent调用异常: {e}", flush=True)
        return "抱歉，处理您的请求时出错了。请稍后重试或换个方式提问~"
