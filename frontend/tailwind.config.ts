import type {Config} from 'tailwindcss';

const config: Config = {
    content: ['./index.html', './src/**/*.{ts,tsx}'],
    theme: {
        extend: {
            colors: {
                accent: {
                    50: '#f8faf0',
                    100: '#eff4de',
                    200: '#dde8b5',
                    300: '#c7d985',
                    400: '#adc751',
                    500: '#8fad33',
                    600: '#6f8c26',
                    700: '#556b1f',
                    800: '#45551d',
                    900: '#3c491d'
                }
            },
            boxShadow: {
                panel: '0 12px 32px rgba(15, 23, 42, 0.12)'
            },
            fontFamily: {
                sans: ['"Space Grotesk"', '"Pretendard"', 'sans-serif']
            }
        }
    },
    plugins: []
};

export default config;
