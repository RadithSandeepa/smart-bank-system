# Use official Node base image
FROM node:18

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application
COPY . .

# Build TypeScript code
RUN npm run build

# Expose port (match your .env PORT)
EXPOSE 4001

# Run the compiled code
CMD ["node", "dist/index.js"]
