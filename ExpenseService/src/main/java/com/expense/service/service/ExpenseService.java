package com.expense.service.service;

import com.expense.service.dto.ExpenseDto;
import com.expense.service.entities.Expense;
import com.expense.service.repository.ExpenseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public boolean createExpense(ExpenseDto expenseDto) {
        setCurrency(expenseDto);
        try {
            Expense ep = expenseRepository.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<ExpenseDto> getExpanssByDate(String userId, Timestamp startdate, Timestamp enddate) {
        List<Expense> expenses = expenseRepository.findByUserIdAndCreatedAtBetween(userId, startdate, enddate);
        return objectMapper.convertValue(expenses, new TypeReference<List<ExpenseDto>>() {
        });
    }

    public boolean updateExpense(ExpenseDto expenseDto) {
        setCurrency(expenseDto);
        Optional<Expense> expenseFoundOpt = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());
        if (expenseFoundOpt.isEmpty()) {
            return false;
        }
        Expense expense = expenseFoundOpt.get();
        expense.setAmount(expenseDto.getAmount());
        expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant()) ? expenseDto.getMerchant() : expense.getMerchant());
        expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency()) ? expenseDto.getCurrency() : expense.getCurrency());
        expense.setChennel(Strings.isNotBlank(expenseDto.getChennel()) ? expenseDto.getChennel() : expense.getChennel());
        expense.setCreatedAt(expenseDto.getCreatedAt());

        expenseRepository.save(expense);
        return true;
    }

    public List<ExpenseDto> getExpenses(String userId) {
        List<Expense> expenseOpt = expenseRepository.findByUserId(userId);
        return objectMapper.convertValue(expenseOpt, new TypeReference<List<ExpenseDto>>() {
        });
    }

    private void setCurrency(ExpenseDto expenseDto) {
        if (Objects.isNull(expenseDto.getCurrency())) {
            expenseDto.setCurrency("INR");
        }
    }

    public ResponseEntity updateExpanseService(ExpenseDto expenseDto) {
        try {
            Optional<Expense> expanseOpt = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());
            if (expanseOpt.isEmpty()) {
                return new ResponseEntity(false, HttpStatus.BAD_REQUEST);
            }
            Expense esvae = objectMapper.convertValue(expenseDto, new TypeReference<Expense>() {
            });
            expenseRepository.delete(expanseOpt.get());
            Expense epdto = expenseRepository.save(esvae);
            return new ResponseEntity<>(epdto,HttpStatus.OK);
        }catch (Exception e){
            System.out.println("unable to get the user");
            return new ResponseEntity<>(false,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity deleteExpanseByUserId(String userId, String externalId){
        try {
            Optional<Expense> expanseOpt = expenseRepository.findByUserIdAndExternalId(userId, externalId);

            if (expanseOpt.isEmpty()) {
                return new ResponseEntity(false, HttpStatus.BAD_REQUEST);
            }
            expenseRepository.delete(expanseOpt.get());
            return new ResponseEntity(true, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
