### Stage 1: builder
##FROM node:18-alpine AS builder
### Use Alpine for small footprint; Node 18 matches your frontend‑maven-plugin Node version :contentReference[oaicite:0]{index=0}
##
##WORKDIR /usr/src/app
##
### Copy only package manifests first to leverage Docker layer caching
##COPY package.json package-lock.json ./
##
### Install all dependencies (including devDependencies) for the build
##RUN npm ci
##
### Copy the rest of the source
##COPY . .
##
### Compile TypeScript to JavaScript
##RUN npm run build
### This runs "tsc -p ." as per your package.json build script :contentReference[oaicite:1]{index=1}
##
### Stage 2: production image
##FROM node:18-alpine
### Again Alpine for a minimal runtime image :contentReference[oaicite:2]{index=2}
##
##WORKDIR /usr/src/app
##
### Only install production dependencies
##COPY package.json package-lock.json ./
##RUN npm ci --only=production
##
### Copy compiled output from builder stage
##COPY --from=builder /usr/src/app/dist ./dist
##
### Copy any other runtime assets (e.g., .env.example, config files)
### COPY --from=builder /usr/src/app/.env.example ./
##
### Set environment to production
##ENV NODE_ENV=production
##
### Expose the port your app listens on (adjust if needed)
##EXPOSE 3000
##
### Run the compiled app
##CMD ["node", "dist/index.js"]
#
#
## Stage 1: builder
#FROM node:18-alpine AS builder
#
## 1. Enable unsafe-perm so npm lifecycle scripts run as root without dropping privileges
#ENV npm_config_unsafe_perm=true
#
#WORKDIR /usr/src/app
#
## 2. Copy package manifests and install with unsafe-perm
#COPY package.json package-lock.json ./
#RUN npm ci --unsafe-perm --include=dev
#
## 3. Copy source files
#COPY . .
#
## 4. Ensure the TypeScript compiler binary is executable
#RUN chmod +x ./node_modules/.bin/tsc
#
## 5. Run the build
#RUN npm run build
#
## Stage 2: production
#FROM node:18-alpine
#
#WORKDIR /usr/src/app
#
## 6. Copy only production dependencies (unsafe-perm also recommended here if you switch users)
#COPY package.json package-lock.json ./
#RUN npm ci --only=production --unsafe-perm --include=dev
#
## 7. Copy compiled output
#COPY --from=builder /usr/src/app/dist ./dist
#
#ENV NODE_ENV=production
#EXPOSE 3000
#
#CMD ["node", "dist/index.js"]


FROM node:18-alpine AS builder
WORKDIR /usr/src/app

# 1) Copy manifests first
COPY package.json package-lock.json ./

# 2) Install all deps (including dev)
RUN npm ci --unsafe-perm --include=dev \
    && npm cache clean --force

# 3) Copy source & build
COPY . .
RUN npm run build

# --- Production Stage ---
FROM node:18-alpine
WORKDIR /usr/src/app

# 4) Only runtime deps
COPY package.json package-lock.json ./
RUN npm ci --only=production --unsafe-perm

# 5) Copy compiled output
COPY --from=builder /usr/src/app/dist ./dist

ENV NODE_ENV=production
EXPOSE 3000
CMD ["node", "dist/index.js"]
