## Use Node.js as the base image
#FROM node:18
#
## Set the working directory
#WORKDIR /app
#
## Copy package.json and package-lock.json
#COPY package*.json ./
#
## Install dependencies
#RUN npm install
#
## Copy the rest of the application code
#COPY . .
#
## Expose the application port
#EXPOSE 5000
#
## Set environment variables
#ENV NODE_ENV=production
#
## Start the application
#CMD ["node", "server.js"]


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
EXPOSE 3000

CMD ["node", "dist/index.js"]
