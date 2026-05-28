"""AI 客服微服务入口"""
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from agent import chat

app = FastAPI(title="日结兼职AI客服", version="1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class ChatRequest(BaseModel):
    message: str
    token: str


class ChatResponse(BaseModel):
    reply: str


@app.post("/ai/chat", response_model=ChatResponse)
async def ai_chat(req: ChatRequest):
    if not req.token:
        raise HTTPException(status_code=401, detail="未提供认证token")
    try:
        reply = chat(req.message, req.token)
        return ChatResponse(reply=reply)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI服务异常: {str(e)}")


@app.get("/ai/health")
async def health():
    return {"status": "ok"}
