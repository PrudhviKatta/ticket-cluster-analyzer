import { create } from 'zustand'
import { persist } from 'zustand/middleware'

// Zustand with localStorage persistence — survives page refresh
const useAuthStore = create(
  persist(
    (set) => ({
      token: null,
      user: null,
      setAuth: (token, user) => set({ token, user }),
      logout: () => set({ token: null, user: null }),
    }),
    { name: 'ticket-analyzer-auth' }
  )
)

export default useAuthStore
