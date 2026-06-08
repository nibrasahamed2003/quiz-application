/**
 * QuizApp - Dynamic Question Form Handler
 */

let questionCount = 0;

function addQuestion() {
    questionCount++;
    const container = document.getElementById('questions-container');

    const questionDiv = document.createElement('div');
    questionDiv.className = 'question-block';
    questionDiv.id = 'question-' + questionCount;

    questionDiv.innerHTML = `
        <span class="question-number">Question ${questionCount}</span>
        ${questionCount > 1 ? `
        <button type="button" class="btn btn-danger btn-sm remove-question" onclick="removeQuestion(${questionCount})">
            <i class="bi bi-x"></i> Remove
        </button>` : ''}

        <div class="mt-3 mb-3">
            <label class="form-label fw-semibold">Question Text</label>
            <textarea name="questionText" class="form-control" rows="2"
                      placeholder="Enter your question here" required></textarea>
        </div>

        <div class="row g-2 mb-2">
            <div class="col-md-6">
                <label class="form-label small fw-semibold">Option A</label>
                <input type="text" name="optionA" class="form-control"
                       placeholder="Enter option A" required>
            </div>
            <div class="col-md-6">
                <label class="form-label small fw-semibold">Option B</label>
                <input type="text" name="optionB" class="form-control"
                       placeholder="Enter option B" required>
            </div>
        </div>
        <div class="row g-2 mb-2">
            <div class="col-md-6">
                <label class="form-label small fw-semibold">Option C</label>
                <input type="text" name="optionC" class="form-control"
                       placeholder="Enter option C" required>
            </div>
            <div class="col-md-6">
                <label class="form-label small fw-semibold">Option D</label>
                <input type="text" name="optionD" class="form-control"
                       placeholder="Enter option D" required>
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                <label class="form-label small fw-semibold">Correct Answer</label>
                <select name="correctAnswer" class="form-select" required>
                    <option value="">Select correct answer...</option>
                    <option value="A">Option A</option>
                    <option value="B">Option B</option>
                    <option value="C">Option C</option>
                    <option value="D">Option D</option>
                </select>
            </div>
        </div>
    `;

    container.appendChild(questionDiv);
}

function removeQuestion(id) {
    const question = document.getElementById('question-' + id);
    if (question) {
        question.remove();
        renumberQuestions();
    }
}

function renumberQuestions() {
    const questions = document.querySelectorAll('.question-block');
    questionCount = questions.length;

    questions.forEach((q, index) => {
        const num = index + 1;
        const numSpan = q.querySelector('.question-number');
        if (numSpan) {
            numSpan.textContent = 'Question ' + num;
        }
    });
}

// Form validation
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('quizForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            const questions = document.querySelectorAll('.question-block');
            if (questions.length === 0) {
                e.preventDefault();
                alert('Please add at least one question to your quiz.');
            }
        });
    }
});
