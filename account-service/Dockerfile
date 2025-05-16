# Stage 1: Dependency installation
FROM node:18-alpine AS deps

WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm ci

# Stage 2: Final image
FROM node:18-alpine

WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm ci --only=production

COPY . .

ENV NODE_ENV=production
EXPOSE 4003

CMD ["node", "server.js"]
