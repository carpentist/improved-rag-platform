# Improved RAG Platform

基于 LangChain4j + Milvus + 通义千问的 Advanced RAG 平台，支持文档向量化入库与检索增强问答。

## 架构

![架构图](image1.png)

## 快速开始

### 1. 启动 Milvus

```bash
docker-compose up -d
```

Milvus 运行在 `localhost:19530`，Attu 管理界面在 `http://localhost:8000`。

### 2. 配置环境变量

```bash
export DASHSCOPE_API_KEY=your-dashscope-api-key
```

或创建 `src/main/resources/application-local.yml`：

```yaml
langchain4j:
  chat-model:
    api-key: sk-xxx
  embedding-model:
    api-key: sk-xxx
milvus:
  host: localhost
```

### 3. 启动应用

```bash
./mvnw spring-boot:run
```

浏览器打开 `http://localhost:8080`。

## API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/rag/ingest` | 上传文档入库 |
| POST | `/api/rag/ingest/{strategy}` | 指定分块策略（RECURSIVE/PARAGRAPH/SENTENCE） |
| POST | `/api/rag/ask` | 检索增强问答 |

### 示例

```bash
# 上传文档
curl -X POST http://localhost:8080/api/rag/ingest -F "file=@doc.pdf"

# 问答
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"面向对象三大特性是什么？"}'
```

启动后浏览器访问 `http://localhost:8080`，可直接在页面上传文档和提问：

![示例截图](img.png)

## 技术栈

- Spring Boot 3.5 + Java 21
- LangChain4j 1.1.0
- Milvus 向量数据库
- 通义千问 (DashScope) / OpenAI 兼容
- Apache Tika 文档解析

## 项目结构

```
src/main/java/com/improvedragplatform/
├── config/          配置（Milvus、Embedding、Chat、Ingestion）
├── ingestion/       文档加载、分块、元数据增强、向量存储
├── retrieval/       查询转换、路由、检索、聚合、注入
├── service/         RetrievalAugmentor 编排
└── controller/      REST API
```

## 切换 LLM 提供商

修改 `application.yml` 中 `langchain4j.*.base-url` 和 `model-name` 即可，支持所有 OpenAI 兼容 API（千问、DeepSeek、硅基流动等）。
