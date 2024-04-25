import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { createLazyFileRoute } from '@tanstack/react-router'
import { ReactNode } from 'react'
import QRCode from 'react-qr-code'

export const Route = createLazyFileRoute('/hotel')({
  component: () => Index()
})

function Index() {
  return (
    <CodeCard code={"LARS"}/>
  )
}

interface CodeCardProps {
  code: String
}
function CodeCard({code}: CodeCardProps) {
  return (
    <Card>
      <div className='flex flex-col items-center m-10 text-center'>

      <h3>Tell code to traveller</h3>
      <h1 className='font-black mx-20 text-5xl text-center'>{code.toUpperCase()}</h1>
      <h3 className='mt-10'>or let them scan the code</h3>
      <div className='mt-5'>
       <QRCode className='' level='H' value={code.toString()}></QRCode>
      </div>
      </div>
    </Card>
  )
}

interface CardProps {
  children: ReactNode
  className?: string
}


function Card({children, className} : CardProps) {
  return (
    <div className={cn(className, 'max-w-sm rounded overflow-hidden bg-zinc-700 flex flex-col items-center')}>
      {children}
    </div>
  )
}