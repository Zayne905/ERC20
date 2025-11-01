// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;


contract ERC20Test {
    // 状态变量
    mapping(address => uint256) private _balances;
    mapping(address => mapping(address => uint256)) private _allowances;
    
    uint256 private _totalSupply;
    string private _name;
    string private _symbol;
    uint8 private _decimals;
    
    address private _owner;

    // 事件定义
    event Transfer(address indexed from, address indexed to, uint256 value);
    event Approval(address indexed owner, address indexed spender, uint256 value);


    constructor( ) {
        _name = "LYCToken";
        _symbol = "YC";
        _decimals = 18;
        _owner = msg.sender;
    }

    // ============ ERC20 标准接口实现 ============


    function name() public view returns (string memory) {
        return _name;
    }


    function symbol() public view returns (string memory) {
        return _symbol;
    }


    function decimals() public view returns (uint8) {
        return _decimals;
    }


    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }


    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }


    function transfer(address to, uint256 value) public returns (bool) {
        address sender = msg.sender;
        _transfer(sender, to, value);
        return true;
    }


    function allowance(address tokenowner, address spender) public view returns (uint256) {
        return _allowances[tokenowner][spender];
    }

    /**
     * @dev 授权其他地址使用代币
     * @param spender 被授权地址
     * @param value 授权数量
     */
    function approve(address spender, uint256 value) public returns (bool) {
        address tokenOwner = msg.sender;
        _approve(tokenOwner, spender, value);
        return true;
    }

    function transferFrom(address from, address to, uint256 value) public returns (bool) {
        address spender = msg.sender;
        _spendAllowance(from, spender, value);
        _transfer(from, to, value);
        return true;
    }

    // ============ 扩展功能 ============

    /**
     * @dev 铸币函数 - 只有合约所有者可以调用
     * @param to 接收代币的地址
     * @param value 铸币数量
     */
    function mint(address to, uint256 value) public {
        require(msg.sender == _owner, "ERC20Test: Only owner can mint");
        _mint(to, value);
    }

    /**
     * @dev 销毁函数 - 用户可以销毁自己拥有的代币
     * @param value 销毁数量
     */
    function burn(uint256 value) public {
        _burn(msg.sender, value);
    }

    /**
     * @dev 从指定地址销毁代币 - 需要授权
     * @param from 销毁代币的地址
     * @param value 销毁数量
     */
    function burnFrom(address from, uint256 value) public {
        _spendAllowance(from, msg.sender, value);
        _burn(from, value);
    }

    /**
     * @dev 获取合约所有者
     */
    function owner() public view returns (address) {
        return _owner;
    }

    /**
     * @dev 转移合约所有权
     * @param newOwner 新的所有者地址
     */
    function transferOwnership(address newOwner) public {
        require(msg.sender == _owner, "ERC20Test: Only owner can transfer ownership");
        require(newOwner != address(0), "ERC20Test: New owner is the zero address");
        _owner = newOwner;
    }

    // ============ 内部函数 ============

    /**
     * @dev 内部转账函数
     */
    function _transfer(address from, address to, uint256 value) internal {
        require(from != address(0), "ERC20Test: transfer from the zero address");
        require(to != address(0), "ERC20Test: transfer to the zero address");

        uint256 fromBalance = _balances[from];
        require(fromBalance >= value, "ERC20Test: transfer amount exceeds balance");
        
        unchecked {
            _balances[from] = fromBalance - value;
            _balances[to] += value;
        }

        emit Transfer(from, to, value);
    }

    /**
     * @dev 内部铸币函数
     */
    function _mint(address account, uint256 value) internal {
        require(account != address(0), "ERC20Test: mint to the zero address");

        _totalSupply += value;
        unchecked {
            _balances[account] += value;
        }
        emit Transfer(address(0), account, value);
    }

    /**
     * @dev 内部销毁函数
     */
    function _burn(address account, uint256 value) internal {
        require(account != address(0), "ERC20Test: burn from the zero address");

        uint256 accountBalance = _balances[account];
        require(accountBalance >= value, "ERC20Test: burn amount exceeds balance");
        
        unchecked {
            _balances[account] = accountBalance - value;
            _totalSupply -= value;
        }

        emit Transfer(account, address(0), value);
    }

    /**
     * @dev 内部授权函数
     */
    function _approve(address tokenOwner, address spender, uint256 value) internal {
        require(tokenOwner != address(0), "ERC20Test: approve from the zero address");
        require(spender != address(0), "ERC20Test: approve to the zero address");

        _allowances[tokenOwner][spender] = value;
        emit Approval(tokenOwner, spender, value);
    }


    /**
     * @dev 内部消耗授权额度函数
     */
    function _spendAllowance(address tokenOwner, address spender, uint256 value) internal {
        uint256 currentAllowance = allowance(tokenOwner, spender);
        if (currentAllowance != type(uint256).max) {
            require(currentAllowance >= value, "ERC20Test: insufficient allowance");
            unchecked {
                _approve(tokenOwner, spender, currentAllowance - value);
            }
        }
    }
}