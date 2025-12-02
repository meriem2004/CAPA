'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Lock, ChevronRight, User } from 'lucide-react'
import { login } from '@/components/declarant/services/api/auth.service'
import { mapBackendRoleToFrontend } from '@/lib/auth-utils'
import { useToast } from '@/hooks/use-toast'

interface LoginPageProps {
  onLogin: (role: 'declarant' | 'qualite' | 'direction', userData?: any) => void
}

export function LoginPage({ onLogin }: LoginPageProps) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { toast } = useToast()

  const handleDemoLogin = (role: 'declarant' | 'qualite' | 'direction') => {
    setIsLoading(true)
    setTimeout(() => {
      onLogin(role)
      setIsLoading(false)
    }, 600)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setIsLoading(true)

    try {
      const response = await login({ username, password })
      
      if (response.message && response.message !== 'Login successful') {
        throw new Error(response.message)
      }

      const frontendRole = mapBackendRoleToFrontend(response.role)
      
      toast({
        title: 'Login successful',
        description: `Welcome, ${response.firstName || response.username}!`,
      })

      onLogin(frontendRole, response)
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || err?.message || 'Invalid username or password'
      setError(errorMessage)
      toast({
        title: 'Login failed',
        description: errorMessage,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 flex items-center justify-center p-4">
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute top-20 right-10 w-72 h-72 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
        <div className="absolute -bottom-20 left-10 w-72 h-72 bg-cyan-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
        <div className="absolute bottom-20 right-20 w-72 h-72 bg-blue-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>
      </div>

      <div className="relative z-10 w-full max-w-md">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-12 h-12 bg-blue-600 rounded-lg mb-4">
            <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-white mb-2">CAPA System</h1>
          <p className="text-slate-300">Quality Management & Corrective Actions</p>
        </div>

        {/* Main Card */}
        <div className="backdrop-blur-md bg-white/10 border border-white/20 rounded-2xl p-8 mb-6 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Error Message */}
            {error && (
              <div className="p-3 bg-red-500/20 border border-red-500/50 rounded-lg text-sm text-red-200">
                {error}
              </div>
            )}

            {/* Username Input */}
            <div className="space-y-2">
              <Label htmlFor="username" className="text-sm font-medium text-slate-100">
                Username
              </Label>
              <div className="relative">
                <User className="absolute left-3 top-3 w-5 h-5 text-slate-400" />
                <Input
                  id="username"
                  type="text"
                  placeholder="Enter your username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="pl-10 bg-white/5 border-white/10 text-white placeholder:text-slate-500 focus:bg-white/10 focus:border-blue-500/50"
                  required
                  disabled={isLoading}
                />
              </div>
            </div>

            {/* Password Input */}
            <div className="space-y-2">
              <Label htmlFor="password" className="text-sm font-medium text-slate-100">
                Password
              </Label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 w-5 h-5 text-slate-400" />
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="pl-10 bg-white/5 border-white/10 text-white placeholder:text-slate-500 focus:bg-white/10 focus:border-blue-500/50"
                  required
                  disabled={isLoading}
                />
              </div>
            </div>

            {/* Sign In Button */}
            <Button
              type="submit"
              disabled={isLoading}
              className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white font-semibold py-2 rounded-lg transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  Signing in...
                </>
              ) : (
                <>
                  Sign In
                  <ChevronRight className="w-4 h-4" />
                </>
              )}
            </Button>
          </form>
        </div>

        {/* Demo Access Section */}
        <div className="space-y-3">
          <p className="text-center text-sm text-slate-400">Or explore with demo access:</p>
          <div className="grid grid-cols-3 gap-3">
            <button
              onClick={() => handleDemoLogin('declarant')}
              disabled={isLoading}
              className="group relative px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/20 rounded-lg text-sm font-medium text-slate-100 transition-all duration-300 disabled:opacity-50"
            >
              <div className="relative z-10">Déclarant</div>
              <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-lg opacity-0 group-hover:opacity-20 transition-opacity"></div>
            </button>
            <button
              onClick={() => handleDemoLogin('qualite')}
              disabled={isLoading}
              className="group relative px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/20 rounded-lg text-sm font-medium text-slate-100 transition-all duration-300 disabled:opacity-50"
            >
              <div className="relative z-10">Qualité</div>
              <div className="absolute inset-0 bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-lg opacity-0 group-hover:opacity-20 transition-opacity"></div>
            </button>
            <button
              onClick={() => handleDemoLogin('direction')}
              disabled={isLoading}
              className="group relative px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/20 rounded-lg text-sm font-medium text-slate-100 transition-all duration-300 disabled:opacity-50"
            >
              <div className="relative z-10">Direction</div>
              <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-cyan-500 rounded-lg opacity-0 group-hover:opacity-20 transition-opacity"></div>
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-8 pt-6 border-t border-white/10 text-center">
          <p className="text-xs text-slate-400">
            This is a demo environment. No data is stored.
          </p>
        </div>
      </div>

      <style jsx>{`
        @keyframes blob {
          0%, 100% {
            transform: translate(0, 0) scale(1);
          }
          33% {
            transform: translate(30px, -50px) scale(1.1);
          }
          66% {
            transform: translate(-20px, 20px) scale(0.9);
          }
        }

        .animate-blob {
          animation: blob 7s infinite;
        }

        .animation-delay-2000 {
          animation-delay: 2s;
        }

        .animation-delay-4000 {
          animation-delay: 4s;
        }
      `}</style>
    </div>
  )
}
