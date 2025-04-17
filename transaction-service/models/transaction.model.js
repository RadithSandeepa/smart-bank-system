import { DataTypes } from "sequelize";
import sequelize from "../config/db.js";

const Transaction = sequelize.define("Transaction", {
  transaction_id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  from_account_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
  },
  to_account_id: {
    type: DataTypes.INTEGER,
    allowNull: false,
  },
  amount: {
    type: DataTypes.DECIMAL(10, 2),
    allowNull: false,
    validate: {
      isDecimal: true,
      min: 0.01,
    }
  },
  transaction_type: {
    type: DataTypes.STRING,
    allowNull: false,
    validate: {
      isIn: [['transfer', 'deposit', 'withdrawal', 'payment']]
    }
  },
  description: {
    type: DataTypes.STRING,
    allowNull: true,
  },
  status: { 
    type: DataTypes.STRING,
    allowNull: false,
    defaultValue: 'completed',
    validate: {
      isIn: [['pending', 'completed', 'failed', 'cancelled']]
    }
  }
}, {
  timestamps: true,
  indexes: [
    { fields: ['from_account_id'] },
    { fields: ['to_account_id'] },
    { fields: ['transaction_type'] },
    { fields: ['createdAt'] }
  ]
});

export default Transaction;