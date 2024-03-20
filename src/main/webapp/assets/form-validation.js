
(() => {
    'use strict'

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll('.needs-validation')

    // Loop over them and prevent submission
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            clearOldMessages(['balance', 'debt', 'overdraft_limit', 'minimum_balance', 'withdraw_amount']);
            if (!form.checkValidity() || !checkCustomValidation(form)) {
                event.preventDefault()
                event.stopPropagation()
            }
            form.classList.add('was-validated')
        }, false)
    })
})()

function checkCustomValidation(form) {
    return (
        validateCheckingAccountForm(form)
        && validateSavingAccountForm(form)
        && validateWithdrawForm(form)
    );
}

function showInvalidMessage(message, fields) {
    Array.from(fields).forEach(id => {
        document.querySelector("#" + id).setCustomValidity("invalid");
        document.querySelector("#" + id + "-feedback").classList.add("hide");
    });
    document.querySelector("#invalid").classList.remove("collapse");
    document.querySelector("#invalid-message").innerText = message;
}

function clearOldMessages(fields) {
    Array.from(fields).forEach(id => {
        const elem = document.querySelector("#" + id);
        if (elem != null) {
            elem.setCustomValidity("");
            document.querySelector("#" + id + "-feedback").classList.remove("hide");
        }
    });
    const elem = document.querySelector("#invalid");
    if (elem != null) {
        elem.classList.add("collapse");
        document.querySelector("#invalid-message").innerText = "";
    }
}

function validateCheckingAccountForm(form) {
    if (form["id"] == "add-checking-account-form") {

        const balance = Number(form["balance"].value);
        const debt = Number(form["debt"].value);
        const overdraftLimit = Number(form["overdraft_limit"].value);

        if (balance > 0.0 && debt > 0.0) {
            showInvalidMessage("Can not have balance while in debt!", ['balance', 'debt']);
            return false;
        }
        if (debt > overdraftLimit) {
            showInvalidMessage("Can not have more debt than overdraft limit!", ['debt', 'overdraft_limit']);
            return false;
        }
    }
    return true;
}

function validateSavingAccountForm(form) {
    if (form["id"] == "add-saving-account-form") {

        const balance = Number(form["balance"].value);
        const minimumBalance = Number(form["minimum_balance"].value);

        if (balance < minimumBalance) {
            showInvalidMessage("Balance can not be less than minimum balance!", ['balance', 'minimum_balance']);
            return false;
        }
    }
    return true;
}