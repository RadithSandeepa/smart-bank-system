/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import Account from "../models/account.model.js";

// Create a new account
export const createAccount = async (req, res) => {
  try {
    const newAccount = await Account.create(req.body);
    return res.status(201).json(newAccount);
  } catch (error) {
    return res.status(500).json({ message: "Failed to create account", error: error.message });
  }
};

// Get account by ID
export const getAccountById = async (req, res) => {
  try {
    const account = await Account.findById(req.params.id);
    if (!account) return res.status(404).json({ message: "Account not found" });
    return res.status(200).json(account);
  } catch (error) {
    return res.status(500).json({ message: "Failed to retrieve account", error: error.message });
  }
};

// Update an account
export const updateAccount = async (req, res) => {
  try {
    const updated = await Account.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updated) return res.status(404).json({ message: "Account not found" });
    return res.status(200).json(updated);
  } catch (error) {
    return res.status(500).json({ message: "Failed to update account", error: error.message });
  }
};

// Delete an account
export const deleteAccount = async (req, res) => {
  try {
    const deleted = await Account.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ message: "Account not found" });
    return res.status(200).json({ message: "Account deleted successfully" });
  } catch (error) {
    return res.status(500).json({ message: "Failed to delete account", error: error.message });
  }
};

// Get all accounts for a user
export const getAccountsByUser = async (req, res) => {
  try {
    const accounts = await Account.find({ userId: req.params.userId });
    return res.status(200).json(accounts);
  } catch (error) {
    return res.status(500).json({ message: "Failed to retrieve accounts", error: error.message });
  }
};
