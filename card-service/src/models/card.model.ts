/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import mongoose, { Document, Schema } from "mongoose";

export interface ICard extends Document {
  accountId: string;
  cardNumber: string;
  cardType: "credit" | "debit";
  expiryDate: Date;
  status: "active" | "blocked";
}

const CardSchema: Schema = new Schema(
  {
    accountId: {
      type: String,
      required: true,
    },
    cardNumber: {
      type: String,
      required: true,
      unique: true,
    },
    cardType: {
      type: String,
      enum: ["credit", "debit"],
      required: true,
    },
    expiryDate: {
      type: Date,
      required: true,
    },
    status: {
      type: String,
      enum: ["active", "blocked"],
      default: "active",
    },
  },
  {
    timestamps: true,
  }
);

export const Card = mongoose.model<ICard>("Card", CardSchema);
