"""调用主后端 API 的工具函数"""
import os
import httpx
from dotenv import load_dotenv

load_dotenv()
BACKEND = os.getenv("MAIN_BACKEND_URL", "http://localhost:8080")


def _headers(token: str) -> dict:
    return {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

KEYS = ["jobName", "jobType", "workAddress", "dailySalary", "workStartTime",
        "workEndTime", "recruitNum", "jobRequire", "id"]

def _trim_jobs(data):
    """精简岗位数据，只保留用户关心的字段，避免返回太大导致LLM 400"""
    jobs = data.get("jobList") or data.get("jobs") or []
    if isinstance(jobs, list):
        data["jobList"] = [{k: j.get(k) for k in KEYS if k in j} for j in jobs]
        data.pop("totalPages", None)
    return data


def search_jobs(token: str, keyword: str = "", job_type: str = "",
                min_salary: int = 0, max_salary: int = 0,
                work_address: str = "", page_num: int = 1, page_size: int = 5) -> dict:
    """搜索已发布的日结岗位。参数均可选，至少提供一个条件。"""
    body = {
        "keyword": keyword,
        "jobType": job_type,
        "minSalary": min_salary,
        "maxSalary": max_salary,
        "workAddress": work_address,
        "pageNum": page_num,
        "pageSize": page_size
    }
    # 过滤掉空值
    body = {k: v for k, v in body.items() if v not in (None, "", 0) or k in ("pageNum", "pageSize")}

    try:
        with httpx.Client(timeout=10) as client:
            r = client.post(f"{BACKEND}/api/job/filter", json=body, headers=_headers(token))
            r.raise_for_status()
            data = r.json()
            if data.get("code") == 200:
                return _trim_jobs(data["data"])
            return {"error": data.get("message", "查询失败")}
    except Exception as e:
        return {"error": f"搜索岗位失败: {str(e)}"}


def get_job_detail(token: str, job_id: int) -> dict:
    """查询单个岗位的详细信息"""
    try:
        with httpx.Client(timeout=10) as client:
            r = client.get(f"{BACKEND}/api/job/detail/{job_id}", headers=_headers(token))
            r.raise_for_status()
            data = r.json()
            if data.get("code") == 200:
                return data["data"]
            return {"error": data.get("message", "岗位不存在")}
    except Exception as e:
        return {"error": f"查询岗位详情失败: {str(e)}"}


def recommend_jobs(token: str) -> dict:
    """为当前登录用户推荐岗位（基于技能标签和地址匹配）"""
    try:
        with httpx.Client(timeout=10) as client:
            r = client.get(f"{BACKEND}/api/job/recommend", headers=_headers(token))
            r.raise_for_status()
            data = r.json()
            if data.get("code") == 200:
                return _trim_jobs({"jobs": data["data"]})
            return {"error": data.get("message", "推荐失败")}
    except Exception as e:
        return {"error": f"推荐岗位失败: {str(e)}"}
