/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'chess-dark': '#312E2B',
        'chess-light': '#EFEFEF',
        'chess-accent': '#769656',
      }
    },
  },
  plugins: [],
}
