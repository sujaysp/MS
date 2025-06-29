package com.eazybytes.loans.service;

import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ILoansService {
    Loans createLoan(String mobileNumber);

    LoansDto getLoanDetails(String mobileNumber);

    boolean updateLoanDetails(LoansDto loansDto);

    boolean deleteLoan(String mobileNumber);
}
