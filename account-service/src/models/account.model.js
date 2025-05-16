/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import mongoose from "mongoose";

const AccountSchema = new mongoose.Schema(
  {
    userId: {
      type: String, 
      required: true,
    },
    accountNumber: {
      type: String,
      required: true,
      unique: true,
    },
    accountType: {
      type: String,
      enum: ["savings", "checking", "business"],
      required: true,
    },
    balance: {
      type: Number,
      required: true,
      min: 0,
      default: 0,
    },
    status: {
      type: String,
      enum: ["active", "frozen", "closed"],
      default: "active",
    },
  },
  {
    timestamps: true,
  }
);

const Account = mongoose.model("Account", AccountSchema);

export default Account;
