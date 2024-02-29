<template>
  <div class="container">
    <h2>Reports</h2>
    <table class="report-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Completed Date</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="report in reports" :key="report.id" class="report-item">
          <td>{{ report.id }}</td>
          <td>{{ formatDate(report.completedDate) }}</td>
          <td>
            <button @click="downloadReport(report.id)" class="download-btn">Download</button>
            <button @click="deleteReport(report.id)" class="delete-btn">Delete</button> <!-- New button to delete one report -->
          </td>
        </tr>
      </tbody>
    </table>
    <button @click="createNewReport" class="create-btn">Create New Report</button>
    <button @click="deleteAllReports" class="delete-all-btn">Delete All Reports</button> <!-- New button to delete all reports -->
  </div>
</template>

<script>
import axios from '../axios-config';

export default {
  data() {
    return {
      reports: []
    };
  },
  created() {
    this.fetchReports();
  },
  methods: {
    fetchReports() {
      axios.get('/api/v1/interview/completed')
        .then(response => {
          this.reports = response.data;
        })
        .catch(error => {
          console.error(error);
        });
    },
    createNewReport() {
      this.$router.push('/new-report');
    },
    downloadReport(reportId) {
      axios.get(`/api/v1/report/${reportId}`, { responseType: 'blob' })
        .then(response => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', `report_${reportId}.docx`);
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(url);
        })
        .catch(error => {
          console.error('Error downloading report:', error);
        });
    },
    deleteReport(reportId) {
      axios.delete(`/api/v1/interview/${reportId}`)
        .then(() => {
          // Remove the deleted report from the reports array
          this.reports = this.reports.filter(report => report.id !== reportId);
          console.log(`Report ${reportId} deleted successfully`);
        })
        .catch(error => {
          console.error('Error deleting report:', error);
        });
    },
    deleteAllReports() {
      axios.delete('/api/v1/interview/all')
        .then(() => {
          // Clear the reports array after deleting all reports
          this.reports = [];
          console.log('All reports deleted successfully');
        })
        .catch(error => {
          console.error('Error deleting all reports:', error);
        });
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: '2-digit' });
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

.report-table {
  margin-bottom: 20px;
  width: 100%;
  border-collapse: collapse;
}

.report-table th,
.report-table td {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  text-align: left;
}

.report-table th {
  background-color: #f2f2f2;
}

.download-btn,
.delete-btn,
.create-btn,
.delete-all-btn {
  margin-right: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
}

.download-btn:hover,
.delete-btn:hover,
.create-btn:hover,
.delete-all-btn:hover {
  background-color: #0056b3;
}

.create-btn,
.delete-all-btn {
  margin-top: 10px;
}

.create-btn:hover,
.delete-all-btn:hover {
  background-color: #218838;
}
</style>
