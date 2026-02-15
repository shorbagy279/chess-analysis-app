import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const analysisAPI = {
  // Start a new analysis
  startAnalysis: async (username, platform, gameCount = 50) => {
    try {
      const response = await api.post('/analysis/start', {
        username,
        platform,
        gameCount,
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to start analysis');
    }
  },

  // Get latest analysis for a user
  getLatestAnalysis: async (username, platform) => {
    try {
      const response = await api.get(`/analysis/user/${username}`, {
        params: { platform },
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to fetch analysis');
    }
  },

  // Health check
  healthCheck: async () => {
    try {
      const response = await api.get('/analysis/health');
      return response.data;
    } catch (error) {
      return { status: 'DOWN' };
    }
  },
};

export default api;
