"""LangGraph 智能客服 Agent（懒加载避免import时卡死）"""
import os
import json
from dotenv import load_dotenv

from langgraph.graph import StateGraph, END
from langgraph.prebuilt import ToolNode
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.tools import tool
from langchain_openai import ChatOpenAI

from tools import search_jobs, get_job_detail, recommend_jobs

load_dotenv()

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
    """懒加载：首次调用时才初始化LLM和Graph"""
    llm = ChatOpenAI(
        model=os.getenv("LLM_MODEL", "deepseek-chat"),
        api_key=os.getenv("LLM_API_KEY"),
        base_url=os.getenv("LLM_BASE_URL", "https://api.deepseek.com"),
        temperature=0.3,
        max_retries=1,
        timeout=30,
    )

    @tool
    def tool_search_jobs(keyword: str = "", job_type: str = "",
                         min_salary: int = 0, max_salary: int = 0,
                         work_address: str = "") -> str:
        """搜索日结兼职岗位。参数均可选：keyword(岗位名称关键词)、job_type(类型如餐饮/快递/家教/零售/促销/安保)、min_salary(最低日薪)、max_salary(最高日薪)、work_address(工作地址模糊匹配)。"""
        result = search_jobs(_get_current_token(), keyword=keyword, job_type=job_type,
                             min_salary=min_salary, max_salary=max_salary,
                             work_address=work_address, page_size=5)
        return json.dumps(result, ensure_ascii=False, indent=2)

    @tool
    def tool_get_job_detail(job_id: int) -> str:
        """查询单个岗位的完整详情。job_id: 岗位ID"""
        result = get_job_detail(_get_current_token(), job_id)
        return json.dumps(result, ensure_ascii=False, indent=2)

    @tool
    def tool_recommend_jobs() -> str:
        """为当前用户推荐最匹配的日结岗位（基于技能标签和地址）"""
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
        print("[AI服务] 正在初始化LangGraph Agent...")
        _agent = _build_agent()
        print("[AI服务] Agent初始化完成")

    messages = []
    if history:
        for h in history:
            role = h.get("role", "user")
            if role == "user":
                messages.append(HumanMessage(content=h["content"]))
            elif role == "assistant":
                messages.append(AIMessage(content=h["content"]))

    messages.append(HumanMessage(content=message))
    result = _agent.invoke({"messages": messages, "token": token})
    return result["messages"][-1].content
