// Custom JavaScript for Expense Split Application

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Form validation
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });


});

// Utility Functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(date) {
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    }).format(new Date(date));
}

// Expense Management Functions
function toggleAllParticipants() {
    const selectAll = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.participant-checkbox');

    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAll.checked;
    });

    // Update form validation
    updateFormValidation();
}

function updateFormValidation() {
    const form = document.querySelector('form[action*="/expenses"]');
    if (form) {
        const checkedBoxes = document.querySelectorAll('.participant-checkbox:checked');
        const submitButton = form.querySelector('button[type="submit"]');

        if (checkedBoxes.length === 0) {
            submitButton.disabled = true;
            submitButton.title = 'Please select at least one participant';
        } else {
            submitButton.disabled = false;
            submitButton.title = '';
        }
    }
}

function viewExpense(expenseId) {
    // Redirect to expense details page
    window.location.href = '/expenses/' + expenseId;
}

function deleteExpense(expenseId, groupId) {
    if (confirm('Are you sure you want to delete this expense? This action cannot be undone.')) {
        fetch('/api/expenses/' + expenseId, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('Expense deleted successfully!');
                window.location.href = '/groups/' + groupId;
            } else {
                alert('Error deleting expense. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting expense. Please try again.');
        });
    }
}

// Make deleteExpense globally available
window.deleteExpense = deleteExpense;

// Add event listeners for participant checkboxes
document.addEventListener('DOMContentLoaded', function() {
    const participantCheckboxes = document.querySelectorAll('.participant-checkbox');
    participantCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateFormValidation);
    });
});

// User Management Functions
function editUser(userId) {
    // Prompt for new details
    const newName = prompt('Enter new name for the user:');
    if (newName === null) return;
    
    const newEmail = prompt('Enter new email for the user:');
    if (newEmail === null) return;
    
    const newContact = prompt('Enter new contact number (optional):') || null;
    
    // Get current user details
    fetch('/api/users/' + userId)
        .then(response => response.json())
        .then(currentUser => {
            // Create updated user object
            const updatedUser = {
                name: newName,
                email: newEmail,
                contactNo: newContact || currentUser.contactNo
            };
            
            // Send update request
            fetch('/api/users/' + userId, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedUser)
            })
            .then(response => {
                if (response.ok) {
                    alert('User updated successfully!');
                    window.location.reload();
                } else {
                    alert('Error updating user. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error updating user. Please try again.');
            });
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error fetching user details.');
        });
}

function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        fetch('/api/users/' + userId, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json'
            }
        })
        .then(response => {
            if (response.ok || response.status === 204) {
                alert('User deleted successfully!');
                window.location.reload();
            } else {
                response.text().then(text => {
                    alert('Error deleting user: ' + text);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting user: ' + error.message);
        });
    }
}

function removeMemberFromGroup(groupId, userId, userName) {
    if (confirm('Are you sure you want to remove ' + userName + ' from this group?')) {
        fetch('/groups/' + groupId + '/members/' + userId, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json'
            }
        })
        .then(response => {
            if (response.ok || response.status === 204 || response.status === 200) {
                alert('Member removed successfully!');
                window.location.reload();
            } else {
                response.text().then(text => {
                    console.error('Error response:', text);
                    alert('Error removing member: ' + text);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error removing member: ' + error.message);
        });
    }
}

// Make functions globally available
window.editUser = editUser;
window.deleteUser = deleteUser;
window.removeMemberFromGroup = removeMemberFromGroup;

// Group Management Functions
function addMemberToGroup(groupId) {
    const form = document.getElementById('addMemberForm');
    if (form) {
        form.action = '/groups/' + groupId + '/members';
        
        const modal = new bootstrap.Modal(document.getElementById('addMemberModal'));
        modal.show();
    }
}

// Balance Calculation Helpers
function calculateSettlements(balances) {
    const creditors = balances.filter(b => b.netBalance > 0).sort((a, b) => b.netBalance - a.netBalance);
    const debtors = balances.filter(b => b.netBalance < 0).sort((a, b) => a.netBalance - b.netBalance);

    const settlements = [];
    let creditorIndex = 0;
    let debtorIndex = 0;

    while (creditorIndex < creditors.length && debtorIndex < debtors.length) {
        const creditor = creditors[creditorIndex];
        const debtor = debtors[debtorIndex];

        const amount = Math.min(creditor.netBalance, Math.abs(debtor.netBalance));

        if (amount > 0) {
            settlements.push({
                from: debtor.userId,
                to: creditor.userId,
                amount: amount
            });

            creditor.netBalance -= amount;
            debtor.netBalance += amount;

            if (creditor.netBalance === 0) creditorIndex++;
            if (debtor.netBalance === 0) debtorIndex++;
        }
    }

    return settlements;
}

// Show Settlement Modal
function showSettlementModal(groupId) {
    const balanceElements = document.querySelectorAll('[data-user-id]');
    const debtors = [];
    const creditors = [];

    balanceElements.forEach(element => {
        const userId = parseInt(element.getAttribute('data-user-id'));
        const userName = element.querySelector('.card-title').textContent.trim();
        const alertElement = element.querySelector('.alert');
        let netBalance = 0;

        if (alertElement.classList.contains('alert-success')) {
            const balanceText = alertElement.querySelector('strong').textContent.trim();
            netBalance = parseFloat(balanceText.replace('$', '').replace(',', ''));
            creditors.push({ userId, userName, netBalance });
        } else if (alertElement.classList.contains('alert-danger')) {
            const balanceText = alertElement.querySelector('strong').textContent.trim();
            netBalance = parseFloat(balanceText.replace('$', '').replace(',', ''));
            debtors.push({ userId, userName, netBalance });
        }
    });

    if (debtors.length === 0) {
        alert('No one owes money - all balances are settled!');
        return;
    }

    // Display settlement input form
    const settlementList = document.getElementById('settlementList');
    settlementList.innerHTML = debtors.map(debtor => `
        <div class="card mb-3">
            <div class="card-body">
                <h6 class="card-title">${debtor.userName} owes $${debtor.netBalance.toFixed(2)}</h6>
                ${creditors.map(creditor => `
                    <div class="row mb-2">
                        <div class="col-6">
                            <label class="form-label">Pay to ${creditor.userName}:</label>
                        </div>
                        <div class="col-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control settlement-input" 
                                       data-from="${debtor.userId}" data-to="${creditor.userId}"
                                       min="0" max="${Math.min(debtor.netBalance, creditor.netBalance).toFixed(2)}" 
                                       step="0.01" placeholder="0.00">
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
        </div>
    `).join('');

    window.currentGroupId = groupId;
    const modal = new bootstrap.Modal(document.getElementById('settlementModal'));
    modal.show();
}

// Confirm Settlements
function confirmSettlements() {
    if (!window.currentGroupId) {
        alert('No group selected.');
        return;
    }

    const settlementInputs = document.querySelectorAll('.settlement-input');
    const settlements = [];

    settlementInputs.forEach(input => {
        const amount = parseFloat(input.value);
        if (amount > 0) {
            settlements.push({
                fromUserId: parseInt(input.dataset.from),
                toUserId: parseInt(input.dataset.to),
                amount: amount
            });
        }
    });

    if (settlements.length === 0) {
        alert('Please enter at least one settlement amount.');
        return;
    }

    fetch(`/settle/${window.currentGroupId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(settlements)
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            alert('Settlements recorded successfully!');
            window.location.reload();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error recording settlements. Please try again.');
    });
}

// Add event listener for confirm button
document.addEventListener('DOMContentLoaded', function() {
    const confirmButton = document.getElementById('confirmSettlements');
    if (confirmButton) {
        confirmButton.addEventListener('click', confirmSettlements);
    }
});

// Make functions globally available
window.showSettlementModal = showSettlementModal;
window.confirmSettlements = confirmSettlements;

// Helper function to find user ID by name (placeholder - needs proper implementation)
function findUserIdByName(name) {
    // This is a placeholder. In a real implementation, you'd have user IDs stored in the DOM or fetch them from an API
    // For now, return a dummy ID based on name
    const nameToIdMap = {
        'Alice': 1,
        'Bob': 2,
        'Charlie': 3,
        // Add more mappings as needed
    };
    return nameToIdMap[name] || null;
}

// Animation Functions
function fadeInElement(element) {
    element.classList.add('fade-in');
}

function slideInElement(element) {
    element.classList.add('slide-in');
}

// Add animations to cards when they come into view
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver(function(entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('fade-in');
        }
    });
}, observerOptions);

// Observe all cards
document.addEventListener('DOMContentLoaded', function() {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        observer.observe(card);
    });
});

// Export functions for global use
window.ExpenseSplit = {
    formatCurrency,
    formatDate,
    toggleAllParticipants,
    updateFormValidation,
    viewUser,
    editUser,
    deleteUser,
    addMemberToGroup,
    viewExpense,
    deleteExpense,
    calculateSettlements,
    showSettlementModal,
    confirmSettlements,
    fadeInElement,
    slideInElement
};

