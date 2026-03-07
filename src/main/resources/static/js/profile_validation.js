document.addEventListener('DOMContentLoaded', function () {
    // Inputs
    const fullName = document.querySelector('#name');
    const email = document.querySelector('#email');
    const address = document.querySelector('#address');
    const phone = document.querySelector('#phone');

    const profileForm = document.querySelector('#form');

    const editBtn = document.querySelector('#edit-btn');
    const saveBtn = document.querySelector('#save-btn');

    // Show/enable inputs on edit
    if (editBtn) {
        editBtn.addEventListener('click', () => {
            fullName.removeAttribute('disabled');
            if (email) email.removeAttribute('disabled');
            address.removeAttribute('disabled');
            phone.removeAttribute('disabled');
            saveBtn.style.display = 'inline-block';
            editBtn.style.display = 'none';
        });
    }

    // Error functions
    function showError(input, message) {
        clearError(input);
        const error = document.createElement('div');
        error.className = 'error-message';
        error.style.color = '#ff4444';
        error.style.fontSize = '12px';
        error.style.marginTop = '5px';
        error.textContent = message;
        input.parentNode.appendChild(error);
        input.style.borderColor = '#ff4444';
    }

    function clearError(input) {
        const error = input.parentNode.querySelector('.error-message');
        if (error) error.remove();
        input.style.borderColor = '';
    }

    // Validators
    function validateFullName(value) {
        if (!value.trim()) return 'Full name cannot be empty';
        if (value.length < 2 || value.length > 50) return 'Full name must be 2-50 characters';
        if (!/^([A-Z][a-z]+)(\s[A-Z][a-z]+)*$/.test(value)) return 'Full name must be capitalized (ex: Nguyen Van A)';
        return null;
    }

    function validateEmail(value) {
        if (!value.trim()) return 'Email cannot be empty';

        const regex = /^(?:"(?:[^\x00-\x1F\x22\x5C\x7F-\xFF]|\\[\x20-\x7E])*"|[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]{1,63}(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+){0,31})@(?:(?=.{1,253}$)(?!.*--)(?:[\p{L}\p{N}](?:[\p{L}\p{N}-]{0,61}[\p{L}\p{N}])?\.)+[\p{L}]{2,63})$/u;

        if (!regex.test(value))
            return 'Invalid email format (ex: dat7075@gmail.com)';

        return null;
    }

    function validateAddress(value) {
        if (!value.trim()) return 'Address cannot be empty';
        if (!/^(?!.*[.,#/^()'\-]{2,})(?!-)[A-Za-z0-9\s.,#/^()'\-]{2,50}$/.test(value)) return 'Invalid address (ex: 123 Chicago, USA)';
        return null;
    }

    function validatePhone(value) {
        if (!value.trim()) return 'Phone cannot be empty';
        const phoneRegex = /^0[1-9]\d{8}$/;
        if (!phoneRegex.test(value)) return 'Phone must have 10 digits, start with 0 (0123456789)';
        return null;
    }

    // Real-time clear error
    [fullName, email, address, phone].forEach(input => {
        if (input) input.addEventListener('input', () => clearError(input));
    });

    // Profile form validation
    if (profileForm) {
        profileForm.addEventListener('submit', function (e) {
            let hasError = false;

            const fullNameError = validateFullName(fullName.value);
            if (fullNameError) { showError(fullName, fullNameError); hasError = true; }

            if (email) {
                const emailError = validateEmail(email.value);
                if (emailError) { showError(email, emailError); hasError = true; }
            }

            const addressError = validateAddress(address.value);
            if (addressError) { showError(address, addressError); hasError = true; }

            const phoneError = validatePhone(phone.value);
            if (phoneError) { showError(phone, phoneError); hasError = true; }

            if (hasError) e.preventDefault();
        });
    }
});