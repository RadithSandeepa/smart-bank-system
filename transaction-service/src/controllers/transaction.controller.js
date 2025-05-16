/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import Transaction from "../models/transaction.model";

// Create a new transaction
export const createTransaction = async (req, res) => {
  try {
    const newTransaction = await Transaction.create(req.body);
    return res.status(201).json(newTransaction);
  } catch (error) {
    return res.status(500).json({ message: "Failed to create transaction", error: error.message });
  }
};

// Get a transaction by ID
export const getTransactionById = async (req, res) => {
  try {
    const transaction = await Transaction.findById(req.params.id);
    if (!transaction) return res.status(404).json({ message: "Transaction not found" });
    return res.status(200).json(transaction);
  } catch (error) {
    return res.status(500).json({ message: "Failed to retrieve transaction", error: error.message });
  }
};

// Update a transaction
export const updateTransaction = async (req, res) => {
  try {
    const updated = await Transaction.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updated) return res.status(404).json({ message: "Transaction not found" });
    return res.status(200).json(updated);
  } catch (error) {
    return res.status(500).json({ message: "Failed to update transaction", error: error.message });
  }
};

// Delete a transaction
export const deleteTransaction = async (req, res) => {
  try {
    const deleted = await Transaction.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ message: "Transaction not found" });
    return res.status(200).json({ message: "Transaction deleted successfully" });
  } catch (error) {
    return res.status(500).json({ message: "Failed to delete transaction", error: error.message });
  }
};

// Get all transactions for an account (either fromAccountId or toAccountId)
export const getTransactionsByAccount = async (req, res) => {
  try {
    const accountId = req.params.accountId;
    const transactions = await Transaction.find({
      $or: [{ fromAccountId: accountId }, { toAccountId: accountId }],
    });
    return res.status(200).json(transactions);
  } catch (error) {
    return res.status(500).json({ message: "Failed to retrieve transactions", error: error.message });
  }
};
