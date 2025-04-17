import Transaction from "../models/transaction.model.js";
import { Op } from "sequelize";
import sequelize from "../config/db.js";

// Create a new transaction
export async function createTransaction(req, res) {
  const { from_account_id, to_account_id, amount, transaction_type, description } = req.body;

  // Validate required fields
  const requiredFields = ["from_account_id", "to_account_id", "amount", "transaction_type"];
  const missingFields = requiredFields.filter((field) => req.body[field] === undefined);
  if (missingFields.length) {
    return res.status(400).json({ error: `Missing fields: ${missingFields.join(", ")}` });
  }

  // Validate amount
  if (isNaN(amount) || parseFloat(amount) <= 0) {
    return res.status(400).json({ error: "Amount must be a positive number" });
  }

  // Validate transaction type
  const validTypes = ['transfer', 'deposit', 'withdrawal', 'payment'];
  if (!validTypes.includes(transaction_type)) {
    return res.status(400).json({ 
      error: `Invalid transaction type. Must be one of: ${validTypes.join(", ")}` 
    });
  }

  try {
    const newTransaction = await Transaction.create({
      from_account_id,
      to_account_id,
      amount,
      transaction_type,
      description: description || ""
    });

    return res.status(201).json({
      message: "Transaction created successfully",
      transaction: newTransaction
    });
  } catch (error) {
    console.error("Error creating transaction:", error);
    return res.status(500).json({ error: "Failed to create transaction", details: error.message });
  }
}

// Retrieve a list of transactions with optional filters
export async function getTransactions(req, res) {
  const { 
    transaction_type, 
    from_date, 
    to_date, 
    from_account_id, 
    to_account_id,
    status,
    page = 1, 
    limit = 20 
  } = req.query;
  
  try {
    const whereClause = {};
    const pagination = {
      offset: (parseInt(page) - 1) * parseInt(limit),
      limit: parseInt(limit)
    };

    // Apply filters
    if (transaction_type) {
      whereClause.transaction_type = transaction_type;
    }
    
    if (from_account_id) {
      whereClause.from_account_id = from_account_id;
    }
    
    if (to_account_id) {
      whereClause.to_account_id = to_account_id;
    }
    
    if (status) {
      whereClause.status = status;
    }

    // Date range filtering
    if (from_date || to_date) {
      whereClause.createdAt = {};
      
      if (from_date) {
        const parsedFrom = new Date(from_date);
        if (isNaN(parsedFrom.getTime())) {
          return res.status(400).json({ error: "Invalid from_date format. Use ISO format." });
        }
        whereClause.createdAt[Op.gte] = parsedFrom;
      }
      
      if (to_date) {
        const parsedTo = new Date(to_date);
        if (isNaN(parsedTo.getTime())) {
          return res.status(400).json({ error: "Invalid to_date format. Use ISO format." });
        }
        whereClause.createdAt[Op.lte] = parsedTo;
      }
    }

    // Get count for pagination
    const count = await Transaction.count({ where: whereClause });
    
    // Get transactions with pagination
    const transactions = await Transaction.findAll({ 
      where: whereClause,
      order: [['createdAt', 'DESC']],
      ...pagination
    });
    
    return res.status(200).json({
      total: count,
      page: parseInt(page),
      limit: parseInt(limit),
      total_pages: Math.ceil(count / parseInt(limit)),
      transactions
    });
  } catch (error) {
    console.error("Error fetching transactions:", error);
    return res.status(500).json({ error: "Failed to fetch transactions", details: error.message });
  }
}

// Retrieve details of a specific transaction
export async function getTransactionById(req, res) {
  const { transaction_id } = req.params;
  
  if (!transaction_id || isNaN(transaction_id)) {
    return res.status(400).json({ error: "Invalid transaction ID" });
  }
  
  try {
    const transaction = await Transaction.findByPk(transaction_id);
    if (!transaction) {
      return res.status(404).json({ error: "Transaction not found" });
    }
    return res.status(200).json(transaction);
  } catch (error) {
    console.error("Error fetching transaction by ID:", error);
    return res.status(500).json({ error: "Failed to fetch transaction", details: error.message });
  }
}

// Get transaction statistics (new function)
export async function getTransactionStats(req, res) {
  try {
    const stats = await Transaction.findAll({
      attributes: [
        'transaction_type',
        [sequelize.fn('COUNT', sequelize.col('transaction_id')), 'count'],
        [sequelize.fn('SUM', sequelize.col('amount')), 'total_amount']
      ],
      group: ['transaction_type']
    });
    
    return res.status(200).json(stats);
  } catch (error) {
    console.error("Error fetching transaction statistics:", error);
    return res.status(500).json({ error: "Failed to fetch transaction statistics", details: error.message });
  }
}