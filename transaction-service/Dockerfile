# Use an official Node.js runtime as the base image.
FROM node:14-alpine

# Set the working directory in the container.
WORKDIR /usr/src/app

# Copy package configuration files to the working directory.
COPY package*.json ./

# Install the application dependencies.
RUN npm install

# Copy the rest of the application source code.
COPY . .

# Expose the port the app runs on.
EXPOSE 3000

# Define the command to run the application.
CMD ["npm", "start"]
# Use the official Node.js LTS Alpine image
FROM node:16-alpine

# Create and set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json (if available)
COPY package*.json ./

# Install dependencies (use npm ci for reproducible builds if using package-lock.json)
RUN npm install --production

# Copy the rest of your application code into the container
COPY . .

# Expose port 3000 to the outside world
EXPOSE 3000

# Define environment variable for the port (optional)
ENV PORT=3000

# Start the application
CMD ["node", "app.js"]
