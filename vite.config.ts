import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import path from "path"
import { TanStackRouterVite } from '@tanstack/router-vite-plugin'
import { nodePolyfills } from 'vite-plugin-node-polyfills'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [nodePolyfills(), TanStackRouterVite(), react()],
    resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
})
