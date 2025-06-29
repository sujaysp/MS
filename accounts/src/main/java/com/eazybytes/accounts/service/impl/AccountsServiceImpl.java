package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapCustomerDtoToCustomer(customerDto, new Customer());

        Optional<Customer> optCustomer = customerRepository.findByMobileNumber(customer.getMobileNumber());
        if (optCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber " + customerDto.getMobileNumber());
        }
//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setCreatedBy("Anonymous");
        Customer savedCustomer = customerRepository.save(customer);
        Accounts newAccount = createNewAccount(savedCustomer);
        accountsRepository.save(newAccount);

    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccounts = new Accounts();
        newAccounts.setCustomerId(customer.getCustomerId());
        newAccounts.setAccountType(AccountConstants.SAVINGS);
        newAccounts.setBranchAddress(AccountConstants.ADDRESS);
        Long accountNumber = 900000000L + new Random().nextInt(1000000);
        newAccounts.setAccountNumber(accountNumber);

//        newAccounts.setCreatedBy("Anonymous");
//        newAccounts.setCreatedAt(LocalDateTime.now());
        return newAccounts;
    }

    @Override
    public CustomerDto fetchAccountDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "MobileNumber", mobileNumber));
        Long customerId = customer.getCustomerId();
        Accounts account = accountsRepository.findByCustomerId(customerId).orElseThrow(() -> new ResourceNotFoundException("Account", "MobileNumber", mobileNumber));
        CustomerDto customerDto = CustomerMapper.mapCustomerToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapAccountsToAccountsDto(account, new AccountsDto()));

        return customerDto;
    }

    @Override
    public boolean updateAccountDetails(CustomerDto customerDto) {
        boolean updateFlag = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        Long accountNumber = accountsDto.getAccountNumber();
        Accounts account = accountsRepository.findById(accountNumber).orElseThrow(() -> new ResourceNotFoundException("Account", "Account Number", accountNumber.toString()));
        AccountsMapper.mapAccountsDtoToAccounts(accountsDto, account);
        accountsRepository.save(account);

        Long customerId = account.getCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer", "CustomerId", customerId.toString()));
        CustomerMapper.mapCustomerDtoToCustomer(customerDto, customer);
        customerRepository.save(customer);
        updateFlag = true;
        return updateFlag;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Customer", "MobileNumber", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
