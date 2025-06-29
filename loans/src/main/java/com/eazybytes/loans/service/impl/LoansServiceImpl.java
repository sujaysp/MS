package com.eazybytes.loans.service.impl;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.mapper.LoanMapper;
import com.eazybytes.loans.repository.LoansRepository;
import com.eazybytes.loans.service.ILoansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class LoansServiceImpl implements ILoansService {

    @Autowired
    private LoansRepository loansRepository;

    @Override
    public Loans createLoan(String mobileNumber) {
        Optional<Loans> loans = loansRepository.findByMobileNumber(mobileNumber);
        if(loans.isPresent()){
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber "+mobileNumber);
        }
        return loansRepository.save(createNewLoan(mobileNumber));
    }

    @Override
    public LoansDto getLoanDetails(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "Mobile Number", mobileNumber));
        return LoanMapper.mapFromLoansToLoansDto(loans, new LoansDto());
    }

    @Override
    public boolean updateLoanDetails(LoansDto loansDto) {
        Loans loans = loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "Loan Number", loansDto.getLoanNumber()));
        Loans updatedLoan = LoanMapper.mapFromLoansDtoToLoans(loansDto, loans);
        loansRepository.save(updatedLoan);
        return true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "Mobile Number", mobileNumber));
        loansRepository.delete(loans);
        return true;
    }

    private Loans createNewLoan(String mobileNumber) {
        Loans newLoan = new Loans();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }
}
