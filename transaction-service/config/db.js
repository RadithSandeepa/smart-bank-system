import { Sequelize } from "sequelize";
import fs from 'fs';
import path from 'path';

// Create data directory if it doesn't exist
const dataDir = path.join(process.cwd(), 'data');
if (!fs.existsSync(dataDir)) {
  fs.mkdirSync(dataDir);
}

// Environment-based configuration
const env = process.env.NODE_ENV || 'development';
const dbConfig = {
  development: {
    dialect: "sqlite",
    storage: path.join(dataDir, "transactions.db"),
    logging: console.log
  },
  test: {
    dialect: "sqlite",
    storage: ":memory:",
    logging: false
  },
  production: {
    dialect: process.env.DB_DIALECT || "postgres",
    host: process.env.DB_HOST || "localhost",
    port: process.env.DB_PORT || 5432,
    username: process.env.DB_USERNAME || "postgres",
    password: process.env.DB_PASSWORD || "postgres",
    database: process.env.DB_NAME || "transactions",
    logging: false,
    dialectOptions: {
      ssl: process.env.DB_SSL === 'true' ? {
        require: true,
        rejectUnauthorized: false
      } : false
    },
    pool: {
      max: 10,
      min: 0,
      acquire: 30000,
      idle: 10000
    }
  }
};

const sequelize = new Sequelize(dbConfig[env]);

export default sequelize;