/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import { Request, Response, NextFunction } from "express";
import { Card } from "../models/card.model";

// Create a new card
export const createCard = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const newCard = await Card.create(req.body);
    res.status(201).json(newCard);
  } catch (error) {
    next(error);
  }
};

// Get a card by ID
export const getCardById = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const card = await Card.findById(req.params.id);
    if (!card) res.status(404).json({ message: "Card not found" });
    res.status(200).json(card);
  } catch (error) {
    next(error);
  }
};

// Update a card
export const updateCard = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const updated = await Card.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updated) res.status(404).json({ message: "Card not found" });
    res.status(200).json(updated);
  } catch (error) {
    next(error);
  }
};

// Delete a card
export const deleteCard = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const deleted = await Card.findByIdAndDelete(req.params.id);
    if (!deleted) res.status(404).json({ message: "Card not found" });
    res.status(200).json({ message: "Card deleted successfully" });
  } catch (error) {
    next(error);
  }
};

// Get all cards for an account
export const getCardsByAccount = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const cards = await Card.find({ accountId: req.params.accountId });
    res.status(200).json(cards);
  } catch (error) {
    next(error);
  }
};
