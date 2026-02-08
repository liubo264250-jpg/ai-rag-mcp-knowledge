# 兼容 amd、arm 构建镜像
docker buildx build --load --platform linux/amd64 -t liubo68/ai-mcp-knowledge-app:1.0 -f ./Dockerfile .