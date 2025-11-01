package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/erc20test")
@RequiredArgsConstructor
public class ERC20TestController {

    private final ERC20TestService erc20TestService;

    /**
     * Deploy a new ERC20Test contract
     * @return the address of the deployed contract
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        return erc20TestService.deployContract()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Load an existing contract
     * @param address the address of the contract to load
     * @return a success message
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        erc20TestService.loadContract(address);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract loaded successfully");
        response.put("contractAddress", address);
        return ResponseEntity.ok(response);
    }

    /**
     * Mint tokens to an address
     * @param to the address to receive tokens
     * @param amount the amount of tokens to mint
     * @return the transaction receipt details
     */
    @PostMapping("/mint")
    public CompletableFuture<ResponseEntity<Map<String, String>>> mint(
            @RequestParam String to,
            @RequestParam BigInteger amount) {
        return erc20TestService.mint(to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Transfer tokens to another address
     * @param to the address to receive tokens
     * @param amount the amount of tokens to transfer
     * @return the transaction receipt details
     */
    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transfer(
            @RequestParam String to,
            @RequestParam BigInteger amount) {
        return erc20TestService.transfer(to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get the balance of an address
     * @param account the address to check balance for
     * @return the balance
     */
    @GetMapping("/balance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> balanceOf(@RequestParam String account) {
        return erc20TestService.balanceOf(account)
                .thenApply(balance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("balance", balance.toString());
                    response.put("account", account);
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Approve another address to spend tokens
     * @param spender the address to approve
     * @param amount the amount to approve
     * @return the transaction receipt details
     */
    @PostMapping("/approve")
    public CompletableFuture<ResponseEntity<Map<String, String>>> approve(
            @RequestParam String spender,
            @RequestParam BigInteger amount) {
        return erc20TestService.approve(spender, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Transfer tokens from one address to another (requires approval)
     * @param from the address to transfer from
     * @param to the address to transfer to
     * @param amount the amount to transfer
     * @return the transaction receipt details
     */
    @PostMapping("/transferFrom")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigInteger amount) {
        return erc20TestService.transferFrom(from, to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get the total supply of tokens
     * @return the total supply
     */
    @GetMapping("/totalSupply")
    public CompletableFuture<ResponseEntity<Map<String, String>>> totalSupply() {
        return erc20TestService.totalSupply()
                .thenApply(totalSupply -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("totalSupply", totalSupply.toString());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get the allowance of a spender for an owner
     * @param owner the owner address
     * @param spender the spender address
     * @return the allowance amount
     */
    @GetMapping("/allowance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> allowance(
            @RequestParam String owner,
            @RequestParam String spender) {
        return erc20TestService.allowance(owner, spender)
                .thenApply(allowance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("allowance", allowance.toString());
                    response.put("owner", owner);
                    response.put("spender", spender);
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get the address of the currently loaded contract
     * @return the contract address
     */
    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getContractAddress() {
        String address = erc20TestService.getContractAddress();
        Map<String, String> response = new HashMap<>();
        if (address != null) {
            response.put("contractAddress", address);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No contract loaded");
            return ResponseEntity.ok(response);
        }
    }
    @PostMapping("/burn")
    public CompletableFuture<ResponseEntity<Map<String, String>>> burn(@RequestParam BigInteger amount) {
        return erc20TestService.burn(amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

}
