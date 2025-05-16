/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import mongoose from "mongoose";

const TransactionSchema = new mongoose.Schema(
  {
    fromAccountId: {
      type: String,
      required: true,
    },
    toAccountId: {
      type: String,
      required: true,
    },
    amount: {
      type: Number,
      required: true,
      min: 0.01,
    },
    transactionType: {
      type: String,
      enum: ["transfer", "deposit", "withdrawal", "payment"],
      required: true,
    },
    description: {
      type: String,
    },
    status: {
      type: String,
      enum: ["pending", "completed", "failed", "cancelled"],
      default: "completed",
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

const Transaction = mongoose.model("Transaction", TransactionSchema);

export default Transaction;
