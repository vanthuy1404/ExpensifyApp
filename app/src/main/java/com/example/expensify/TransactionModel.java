package com.example.expensify;

import android.text.format.Time;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;

public class TransactionModel {

    private String id, categoryDetail, categoryId, note, userId, walletId, createdAt, updatedAt;
    private Double amount;

//    public TransactionModel(String id, String categoryDetail, String categoryId, String note, String userId, String walletId, String amount, String createdAt, String updatedAt) {
    public TransactionModel(String id, String categoryDetail, String note, Double amount, String categoryId, String userId, String walletId, String createdAt, String updatedAt) {
        this.id = id;
        this.categoryDetail = categoryDetail;
        this.note = note;
        this.amount = amount;
        this.categoryId = categoryId;
        this.userId = userId;
        this.walletId = walletId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryDetail() {
        return categoryDetail;
    }

    public void setCategoryDetail(String categoryDetail) {
        this.categoryDetail = categoryDetail;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
