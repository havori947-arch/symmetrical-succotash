from typing import Optional
from langchain_mistralai import ChatMistralAI
from langchain_core.pydantic_v1 import BaseModel, Field

class Expense(BaseModel):
    """Information about a transaction made on any Card"""
    amount: Optional[str] = Field(title="expense", description="Expense made on the transaction which will be a decimal number with exactly two digits after the decimal point")
    merchant: Optional[str] = Field(title="merchant", description="Extract the merchant name (if present) from the following bank SMS message and return the marchent name else the return null only")
    chennel: Optional[str] = Field(title="chennel", description="Extract the payment channel (e.g. ATM, Card, POS, Net Banking, UPI, Mobile Banking, etc.) and return it")
    currency: Optional[str] = Field(title="currency", description="currency of the transaction which can be present before or after the amount and Convert the given currency name, symbol, or abbreviation into its corresponding ISO 4217 currency code. If the input is ambiguous or not recognized, return null only")
    transaction_type: Optional[str] = Field(title="transaction_type", description="check the message is describing a debited or credited transation, if debited then return 'Debit' , if credited then return 'Credit' else return null only ")

    def serialize(self):
        return {
            "amount": self.amount,
            "merchant": self.merchant,
            "chennel": self.chennel,
            "currency": self.currency,
            "transaction_type": self.transaction_type
        }