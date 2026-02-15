import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// User Analysis API
export const analyzeUser = async (username, platform, numberOfGames) => {
  try {
    const response = await api.post('/analysis/user/start', {
      username,
      platform,
      numberOfGames,
    });
    return response.data;
  } catch (error) {
    console.error('Error analyzing user:', error);
    throw error.response?.data?.message || 'Failed to analyze user games';
  }
};

// Game Analysis API (NEW!)
export const analyzeGame = async (pgn) => {
  try {
    const response = await api.post('/analysis/game', {
      pgn,
    });
    return response.data;
  } catch (error) {
    console.error('Error analyzing game:', error);
    throw error.response?.data?.message || 'Failed to analyze game';
  }
};

export default api;
