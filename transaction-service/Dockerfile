## Use an official Node.js runtime as the base image.
#FROM node:14-alpine
#
## Set the working directory in the container.
#WORKDIR /usr/src/app
#
## Copy package configuration files to the working directory.
#COPY package*.json ./
#
## Install the application dependencies.
#RUN npm install
#
## Copy the rest of the application source code.
#COPY . .
#
## Expose the port the app runs on.
#EXPOSE 3000
#
## Define the command to run the application.
#CMD ["npm", "start"]
## Use the official Node.js LTS Alpine image
#FROM node:16-alpine
#
## Create and set the working directory inside the container
#WORKDIR /usr/src/app
#
## Copy package.json and package-lock.json (if available)
#COPY package*.json ./
#
## Install dependencies (use npm ci for reproducible builds if using package-lock.json)
#RUN npm install --production
#
## Copy the rest of your application code into the container
#COPY . .
#
## Expose port 3000 to the outside world
#EXPOSE 3000
#
## Define environment variable for the port (optional)
#ENV PORT=3000
#
## Start the application
#CMD ["node", "app.js"]


# # Stage 1: builder
# FROM node:18-alpine AS builder

# # 1. Enable unsafe-perm so npm lifecycle scripts run as root without dropping privileges
# ENV npm_config_unsafe_perm=true

# WORKDIR /usr/src/app

# # 2. Copy package manifests and install with unsafe-perm
# COPY package.json package-lock.json ./
# RUN npm ci --unsafe-perm

# # 3. Copy source files
# COPY . .

# # 4. Ensure the TypeScript compiler binary is executable
# RUN chmod +x ./node_modules/.bin/tsc

# # 5. Run the build
# RUN npm run build

# # Stage 2: production
# FROM node:18-alpine

# WORKDIR /usr/src/app

# # 6. Copy only production dependencies (unsafe-perm also recommended here if you switch users)
# COPY package.json package-lock.json ./
# RUN npm ci --only=production --unsafe-perm

# # 7. Copy compiled output
# COPY --from=builder /usr/src/app/dist ./dist

# ENV NODE_ENV=production
# EXPOSE 3000

# CMD ["node", "dist/index.js"]

# ─── Stage 1: install deps ────────────────────────────────────────────────
FROM node:18-alpine AS deps

WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm ci

# ─── Stage 2: production image ────────────────────────────────────────────
FROM node:18-alpine

WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm ci --only=production

COPY . .

ENV NODE_ENV=production
EXPOSE 4002
CMD ["node", "app.js"]
