<template>
  <div class="container">
    <h2>All Reports</h2>
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
          </td>
        </tr>
      </tbody>
    </table>
    <button @click="createNewReport" class="create-btn">Create New Report</button>
  </div>
</template>

<script>
import axios from '../axios-config';
import router from '../router';

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
      axios.get('/interview/completed')
        .then(response => {
          this.reports = response.data;
        })
        .catch(error => {
          console.error(error);
        });
    },
    createNewReport() {
      router.push('/new-report');
    },
    downloadReport(reportId) {
      axios.get(`/report/${reportId}`, { responseType: 'blob' })
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

.download-btn {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
}

.download-btn:hover {
  background-color: #0056b3;
}

.create-btn {
  background-color: #28a745;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.create-btn:hover {
  background-color: #218838;
}
</style>
