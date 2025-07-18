<template>
  <div>
    <div class="mb-4 d-flex align-items-center justify-content-between">
      <div>
        <h5 class="mb-0">일별 게시글 등록 수</h5>
        <small class="text-muted">최근 {{ selectedPeriod }}일간 추이</small>
      </div>
      <div class="d-flex align-items-center">
        <label class="me-2 text-muted">기간:</label>
        <select v-model="selectedPeriod" class="form-select" style="width: 120px" @change="fetchData">
          <option v-for="p in availablePeriods" :key="p" :value="p">최근 {{ p }}일</option>
        </select>
      </div>
    </div>

    <div class="chart-container" style="height: 400px; position: relative;">
      <Bar :data="chartData" :options="chartOptions" />
    </div>

    <!-- 통계 요약 -->
    <div class="row mt-4">
      <div class="col-md-3">
        <div class="card border-0 bg-light">
          <div class="card-body text-center p-3">
            <div class="h5 mb-1 text-primary">{{ totalBoards }}</div>
            <div class="small text-muted">총 게시글 수</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 bg-light">
          <div class="card-body text-center p-3">
            <div class="h5 mb-1 text-success">{{ avgBoards }}</div>
            <div class="small text-muted">일평균 게시글</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 bg-light">
          <div class="card-body text-center p-3">
            <div class="h5 mb-1 text-info">{{ maxBoards }}</div>
            <div class="small text-muted">최대 일일 게시글</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 bg-light">
          <div class="card-body text-center p-3">
            <div class="h5 mb-1" :class="growthClass">{{ growth }}%</div>
            <div class="small text-muted">전일 대비 증감</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const selectedPeriod = ref(7) // 기본값 7일
const availablePeriods = [7, 14, 30]
const currentData = ref([])

const chartData = ref({
  labels: [],
  datasets: []
})

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    intersect: false,
    mode: 'index'
  },
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: '#ffffff',
      bodyColor: '#ffffff',
      borderColor: '#36a2eb',
      borderWidth: 1,
      cornerRadius: 8,
      displayColors: false,
      callbacks: {
        title: function(context) {
          return `${context[0].label.replace('-', '월 ').replace('-', '일')}`
        },
        label: function(context) {
          return `게시글 수: ${context.parsed.y.toLocaleString()}개`
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: '#6c757d',
        font: {
          size: 12
        },
        maxRotation: 0,
        callback: function(value, index) {
          const label = this.getLabelForValue(value)
          return label.replace('-', '/').replace('-', '/')
        }
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: '#f8f9fa',
        borderDash: [3, 3]
      },
      ticks: {
        color: '#6c757d',
        font: {
          size: 12
        },
        callback: function(value) {
          return value.toLocaleString() + '개'
        }
      }
    }
  }
})

// 통계 계산
const totalBoards = computed(() => {
  return currentData.value.reduce((sum, item) => sum + (item.newBoards || 0), 0)
})

const avgBoards = computed(() => {
  if (currentData.value.length === 0) return 0
  return Math.round(totalBoards.value / currentData.value.length)
})

const maxBoards = computed(() => {
  if (currentData.value.length === 0) return 0
  return Math.max(...currentData.value.map(item => item.newBoards || 0))
})

const growth = computed(() => {
  if (currentData.value.length < 2) return 0
  const today = currentData.value[currentData.value.length - 1]?.newBoards || 0
  const yesterday = currentData.value[currentData.value.length - 2]?.newBoards || 0
  if (yesterday === 0) return 0
  return Math.round(((today - yesterday) / yesterday) * 100)
})

const growthClass = computed(() => {
  if (growth.value > 0) return 'text-success'
  if (growth.value < 0) return 'text-danger'
  return 'text-muted'
})

const generateLabels = (days) => {
  const today = new Date()
  const result = []
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(d.getDate() - i)
    // UTC 대신 로컬 시간 사용
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    result.push(`${month}-${day}`)
  }
  return result
}

const mapDataToLabels = (dataset, labels) => {
  const map = Object.fromEntries(dataset.map(d => [d.date.slice(5), d.newBoards]))
  return labels.map(label => map[label] ?? 0)
}

const fetchData = async () => {
  try {
    const res = await axios.get('/v1/admin/dashboard/board/daily', { 
      params: { days: selectedPeriod.value } 
    })
    const data = res.data
    currentData.value = data

    const labels = generateLabels(selectedPeriod.value)
    const chartDataValues = mapDataToLabels(data, labels)

    chartData.value = {
      labels: labels,
      datasets: [{
        label: '게시글 수',
        data: chartDataValues,
        backgroundColor: 'rgba(54, 162, 235, 0.7)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 2,
        borderRadius: 4,
        borderSkipped: false,
        hoverBackgroundColor: 'rgba(54, 162, 235, 0.9)',
        hoverBorderColor: 'rgba(54, 162, 235, 1)',
        hoverBorderWidth: 3
      }]
    }
  } catch (error) {
    console.error('데이터 가져오기 실패:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.chart-container {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.form-select {
  border-radius: 6px;
  border: 1px solid #dee2e6;
  box-shadow: none;
  font-size: 14px;
}

.form-select:focus {
  border-color: #36a2eb;
  box-shadow: 0 0 0 0.2rem rgba(54, 162, 235, 0.25);
}

.card {
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.card:hover {
  transform: translateY(-2px);
}
</style>