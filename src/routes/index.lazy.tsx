import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { createLazyFileRoute } from '@tanstack/react-router'
import { ReactNode, createRef, useState } from 'react'
import { Scanner } from "@yudiel/react-qr-scanner"
import { Buffer } from 'buffer'
import { Dialog, DialogTrigger, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Check } from 'lucide-react'
export const Route = createLazyFileRoute('/')({
  component: () => ScannerContainer()
})

function ScannerContainer() {
  const [privKey, setPrivKey] = useState<undefined | String>(undefined)
  const inputRef = createRef<HTMLInputElement>()
  if (!privKey) {
    return (<Dialog open >
    <DialogContent className="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>Please input your PrivateKey</DialogTitle>
      </DialogHeader>
      <div className="grid gap-4 py-4">
        <div className="grid grid-cols-4 items-center gap-4">
          <Label htmlFor="privKey" className="text-right">
            Private Key
          </Label>
          <Input ref={inputRef} id="name" className="col-span-3" />
        </div>
      </div>
      <DialogFooter>
        <Button onClick={() => {
          let text = inputRef?.current?.value
          if (!!text && text.length > 0) {
            setPrivKey(text)
            // validate()
          }}}>Save</Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>)
  }
  else {
    return (<AuthCodeScanner/>)
  }


}

function PkeyInputModal() {

}

function AuthCodeScanner() {
  const [data, setData] = useState('Please scan sth :3')
  const [error, setError] = useState('')
  const [showVerifiedDialog, setShowVerifiedDialog] = useState(false)

  if (showVerifiedDialog) { 
    return (
    <Dialog open>
      <DialogContent>
        <div className='flex justify-center'>
        <Check size={90} color='green'/>
        </div>
        <DialogFooter>
        <Button onClick={() => {
          setData('')
          setError('')
          setShowVerifiedDialog(false)
          }}>Cool!</Button>
      </DialogFooter>
      </DialogContent>
    </Dialog>
    )
  } else {
    return (
    <Scanner
      options={{

      }}
      components={{
        torch: false
      }}
      onResult={(text, result) => {
        setData(text);
        setShowVerifiedDialog(true)
      }}
      onError={(error: any) => setError(error?.message)}
      styles={
        {
          container: {
            width: '100vw',
            margin: 'auto'
          }
        }
      }
    />
    )
  }

}
