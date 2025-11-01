package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.erc20test.ERC20Test;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ERC20TestService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    private ERC20Test contract;

    /**
     * -- GETTER --
     *  Get the address of the currently loaded contract
     *
     * @return the contract address
     */
    @Getter
    private String contractAddress;

    /**
     * Deploy the ERC20Test contract to the blockchain
     * @return the address of the deployed contract
     */
    public CompletableFuture<String> deployContract() {
        log.info("Deploying ERC20Test contract...");
        // 使用无参数部署，因为合约构造函数是无参数的
        return ERC20Test.deploy(web3j, credentials, gasProvider)
                .sendAsync()
                .thenApply(contract -> {
                    this.contract = contract;
                    this.contractAddress = contract.getContractAddress();
                    log.info("ERC20Test contract deployed to: {}", contractAddress);
                    return contractAddress;
                });
    }

    /**
     * Load an existing contract from the blockchain
     * @param contractAddress the address of the contract to load
     */
    public void loadContract(String contractAddress) {
        log.info("Loading ERC20Test contract from address: {}", contractAddress);
        this.contract = ERC20Test.load(contractAddress, web3j, credentials, gasProvider);
        this.contractAddress = contractAddress;
    }

    /**
     * Mint tokens to an address
     * @param to the address to receive tokens
     * @param amount the amount of tokens to mint
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> mint(String to, BigInteger amount) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Minting {} tokens to {}", amount, to);
        return contract.mint(to, amount).sendAsync();
    }

    /**
     * Transfer tokens to another address
     * @param to the address to receive tokens
     * @param amount the amount of tokens to transfer
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> transfer(String to, BigInteger amount) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Transferring {} tokens to {}", amount, to);
        return contract.transfer(to, amount).sendAsync();
    }

    /**
     * Get the balance of an address
     * @param account the address to check balance for
     * @return the balance
     */
    public CompletableFuture<BigInteger> balanceOf(String account) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Getting balance for account: {}", account);
        return contract.balanceOf(account).sendAsync();
    }

    /**
     * Approve another address to spend tokens
     * @param spender the address to approve
     * @param amount the amount to approve
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> approve(String spender, BigInteger amount) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Approving {} tokens for spender: {}", amount, spender);
        return contract.approve(spender, amount).sendAsync();
    }

    /**
     * Transfer tokens from one address to another (requires approval)
     * @param from the address to transfer from
     * @param to the address to transfer to
     * @param amount the amount to transfer
     * @return the transaction receipt
     */

    /**
     * Transfer tokens from one address to another (requires approval)
     * @param from the address to transfer from
     * @param to the address to transfer to
     * @param amount the amount to transfer
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> transferFrom(String from, String to, BigInteger amount) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }

        log.info("Starting transferFrom: {} tokens from {} to {}", amount, from, to);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 检查发送方余额
                BigInteger fromBalance = contract.balanceOf(from).send();
                log.info("From address balance: {}", fromBalance);
                if (fromBalance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient balance in from address. Balance: " + fromBalance + ", required: " + amount);
                }

                // 2. 检查授权额度
                BigInteger currentAllowance = contract.allowance(from, credentials.getAddress()).send();
                log.info("Current allowance for spender {}: {}", credentials.getAddress(), currentAllowance);
                if (currentAllowance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient allowance. Current: " + currentAllowance + ", required: " + amount);
                }

                // 3. 执行 transferFrom
                TransactionReceipt receipt = contract.transferFrom(from, to, amount).send();

                if (!receipt.isStatusOK()) {
                    throw new RuntimeException("TransferFrom transaction failed with status: " + receipt.getStatus());
                }

                log.info("TransferFrom successful. TxHash: {}", receipt.getTransactionHash());
                return receipt;

            } catch (Exception e) {
                log.error("TransferFrom failed: ", e);
                throw new RuntimeException("TransferFrom operation failed: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get the total supply of tokens
     * @return the total supply
     */
    public CompletableFuture<BigInteger> totalSupply() {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Getting total supply");
        return contract.totalSupply().sendAsync();
    }

    /**
     * Get the allowance of a spender for an owner
     * @param owner the owner address
     * @param spender the spender address
     * @return the allowance amount
     */
    public CompletableFuture<BigInteger> allowance(String owner, String spender) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Getting allowance for owner {} and spender {}", owner, spender);
        return contract.allowance(owner, spender).sendAsync();
    }

    /**
     * Burn tokens (新增burn方法)
     * @param amount the amount of tokens to burn
     * @return the transaction receipt
     */
    public CompletableFuture<TransactionReceipt> burn(BigInteger amount) {
        if (contract == null) {
            throw new IllegalStateException("Contract not deployed or loaded");
        }
        log.info("Burning {} tokens", amount);
        return contract.burn(amount).sendAsync();
    }

}