<template>
  <div class="container">
    <h2>New Report</h2>
    <div class="progress-bar">
      <div v-for="(section, index) in sections" :key="section.id" :class="stepClasses(index)">
        {{ section }}
      </div>
    </div>
    <form @submit.prevent="submitAnswers" class="form">
      <div v-for="questionResponse in questionResponses" :key="questionResponse.id" class="question">
        <label :for="'response-' + questionResponse.id" class="question-label">{{ questionResponse.text }}</label>
        <template v-if="questionResponse.type === 'RADIO_BUTTON'">
          <div v-for="(option, index) in questionResponse.answerOptions" :key="index" class="option">
            <input type="radio" :id="'response-' + questionResponse.id + '-' + index" :name="'response-' + questionResponse.id" :value="option" v-model="questionResponse.response" required class="radio">
            <label :for="'response-' + questionResponse.id + '-' + index" class="option-label">{{ option }}</label>
          </div>
        </template>
        <template v-else-if="questionResponse.type === 'DATE'">
          <input type="date" :id="'response-' + questionResponse.id" v-model="questionResponse.response" required class="input">
        </template>
        <template v-else-if="questionResponse.type === 'SHORT_ANSWER'">
          <input type="text" :id="'response-' + questionResponse.id" v-model="questionResponse.response" required class="input">
        </template>
        <template v-else-if="questionResponse.type === 'DROPDOWN'">
          <select :id="'response-' + questionResponse.id" v-model="questionResponse.response" required class="select">
            <option v-for="(option, index) in questionResponse.answerOptions" :key="index" :value="option">{{ option }}</option>
          </select>
        </template>
        <template v-else-if="questionResponse.type === 'LONG_ANSWER'">
          <textarea :id="'response-' + questionResponse.id" v-model="questionResponse.response" required class="textarea"></textarea>
        </template>
        <template v-else-if="questionResponse.type === 'TIME'">
          <input type="time" :id="'response-' + questionResponse.id" v-model="questionResponse.response" required class="input">
        </template>
      </div>
      <button type="submit" class="submit-btn">Next</button>
    </form>
  </div>
</template>

<script>
import axios from '../axios-config';
import router from '../router';

export default {
  data() {
    return {
      currentStepIndex: 0,
      interviewId: null,
      sections: [],
      questionResponses: [],
    };
  },
  created() {
    this.startNewInterview();
  },
  methods: {
    startNewInterview() {
      axios.post('api/v1/interview')
        .then(response => {
          this.interviewId = response.data.interviewId;
          this.sections = response.data.sections;
          this.questionResponses = response.data.questionResponses.map(question => ({
            ...question,
            response: question.type === 'CHECK_BOX' ? {} : ''
          }));
          this.currentStepIndex = this.getSectionIndex();
        })
        .catch(error => {
          console.error(error);
        });
    },
    submitAnswers() {
      const interviewId = this.interviewId;
      const questionResponsesDTO = this.questionResponses.map(question => ({
        questionId: question.id,
        response: question.response
      }));

      const saveInterviewAnswersRequest = {
        interviewId: interviewId,
        questionResponses: questionResponsesDTO
      };

      axios.post('/api/v1/interview/responses', saveInterviewAnswersRequest)
        .then(response => {
          if (response.data && response.data.length > 0) {
            this.questionResponses = response.data.map(question => ({
              ...question,
              response: ''
            }));
            this.currentStepIndex = this.getSectionIndex();
          } else {
            router.push('/reports');
          }
        })
        .catch(error => {
          console.error('Error submitting answers:', error);
        });
    },
    getSectionIndex() {
      return this.sections.findIndex(section => {
        return this.questionResponses.find(question => question.section === section);
      });
    },
    stepClasses(index) {
      return {
        'step': true,
        'completed': index < this.currentStepIndex,
        'current': index === this.currentStepIndex
      };
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 5px;
}

.progress-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.step {
  flex: 1;
  background-color: #f0f0f0;
  padding: 10px 20px;
  border-radius: 5px;
  text-align: center;
}

.completed {
  background-color: #28a745;
  color: #fff;
}

.current {
  background-color: #007bff;
  color: #fff;
}

.question {
  margin-bottom: 20px;
}

.question-label {
  font-weight: bold;
}

.option {
  margin-top: 5px;
}

.radio,
.checkbox {
  margin-right: 5px;
}

.submit-btn {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-btn:hover {
  background-color: #0056b3;
}

.input,
.select,
.textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  margin-top: 5px;
}

.input:focus,
.select:focus,
.textarea:focus {
  outline: none;
  border-color: #007bff;
}
</style>