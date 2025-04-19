## Use official Node base image
#FROM node:18
#
#ENV npm_config_unsafe_perm=true
#
## Set working directory
#WORKDIR /app
#
## Copy package files and install dependencies
#COPY package*.json ./
#RUN npm install
#
## Copy the rest of the application
#COPY . .
#
## Build TypeScript code
#RUN npm run build
#
## Expose port (match your .env PORT)
#EXPOSE 4001
#
## Run the compiled code
#CMD ["node", "dist/index.js"]


# Stage 1: builder
FROM node:18-alpine AS builder

# 1. Enable unsafe-perm so npm lifecycle scripts run as root without dropping privileges
ENV npm_config_unsafe_perm=true

WORKDIR /usr/src/app

# 2. Copy package manifests and install with unsafe-perm
COPY package.json package-lock.json ./
RUN npm ci --unsafe-perm

# 3. Copy source files
COPY . .

# 4. Ensure the TypeScript compiler binary is executable
RUN chmod +x ./node_modules/.bin/tsc

# 5. Run the build
RUN npm run build

# Stage 2: production
FROM node:18-alpine

WORKDIR /usr/src/app

# 6. Copy only production dependencies (unsafe-perm also recommended here if you switch users)
COPY package.json package-lock.json ./
RUN npm ci --only=production --unsafe-perm

# 7. Copy compiled output
COPY --from=builder /usr/src/app/dist ./dist

ENV NODE_ENV=production
EXPOSE 4001

CMD ["node", "dist/index.js"]
