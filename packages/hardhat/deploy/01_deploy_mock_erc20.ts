import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

/**
 * Deploys a contract named "YourContract" using the deployer account and
 * constructor arguments set to the deployer address
 *
 * @param hre HardhatRuntimeEnvironment object.
 */
const deployYourContract: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  /*
    On localhost, the deployer account is the one that comes with Hardhat, which is already funded.

    When deploying to live networks (e.g `yarn deploy --network sepolia`), the deployer account
    should have sufficient balance to pay for the gas fees for contract creation.

    You can generate a random account with `yarn generate` or `yarn account:import` to import your
    existing PK which will fill DEPLOYER_PRIVATE_KEY_ENCRYPTED in the .env file (then used on hardhat.config.ts)
    You can run the `yarn account` command to check your balance in every network.
  */
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  console.log(` 正在使用部署者账号: ${deployer} 部署 ERC20Test 合约`);


  await deploy("ERC20Test", {
    from: deployer,
    // Contract constructor arguments
    
    args: [],
    log: true,
    // autoMine: can be passed to the deploy function to make the deployment process faster on local networks by
    // automatically mining the contract deployment transaction. There is no effect on live networks.
    autoMine: true,
  });

  
    // 3. 部署后验证：获取合约实例并查询核心信息（确保部署成功）
  const erc20Test = await hre.ethers.getContract<Contract>("ERC20Test", deployer);
  const tokenName = await erc20Test.name();
  const tokenSymbol = await erc20Test.symbol();
  const tokenDecimals = await erc20Test.decimals();
  console.log(` ERC20Test 合约部署验证：`);
  console.log(`- 代币名称: ${tokenName}`);
  console.log(`- 代币符号: ${tokenSymbol}`);
  console.log(`- 小数位数: ${tokenDecimals}`);
  console.log(`- 合约地址: ${await erc20Test.getAddress()}`); // 记录此地址，后续可在 POTOS 区块浏览器查询

  // Get the deployed contract to interact with it after deploying.
  const yourContract = await hre.ethers.getContract<Contract>("YourContract", deployer);
  console.log("👋 Initial greeting:", await yourContract.greeting());
};

export default deployYourContract;

// Tags are useful if you have multiple deploy files and only want to run one of them.
// e.g. yarn deploy --tags YourContract
deployYourContract.tags = ["ERC20LYC202330550952"];

