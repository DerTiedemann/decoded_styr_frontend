import { Button } from "@/components/ui/button";
import { createLazyFileRoute } from "@tanstack/react-router";
import { createRef, useEffect, useState } from "react";
import { Scanner } from "@yudiel/react-qr-scanner";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Ban, Check } from "lucide-react";
import { keccak_256 } from "@noble/hashes/sha3";

import * as secp from "@noble/secp256k1";
export const Route = createLazyFileRoute("/")({
  component: () => ScannerContainer(),
});

function ScannerContainer() {
  const [privKey, setPrivKey] = useState<undefined | String>(undefined);
  const [pubKey, setPubKey] = useState<undefined | String>(undefined);
  const [ethAddress, setEthAddress] = useState<undefined | String>(undefined);
  const inputRef = createRef<HTMLInputElement>();
  if (!privKey || !pubKey || !ethAddress) {
    return (
      <Dialog open>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Please input your PrivateKey</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="privKey" className="text-right">
                Private Key
              </Label>
              <Input
                ref={inputRef}
                type="password"
                id="name"
                className="col-span-3"
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              onClick={async () => {
                let text = inputRef?.current?.value;
                if (!!text && text.length > 0) {
                  // let bt = BinTools.getInstance();
                  if (secp.utils.isValidPrivateKey(text)) {
                    setPrivKey(text);
                    let pubKey = secp.getPublicKey(text, false);
                    let hexPubkey = secp.etc.bytesToHex(pubKey);
                    setPubKey(hexPubkey);
                    let ethAddress =
                      "0x" +
                      secp.etc.bytesToHex(
                        keccak_256(pubKey.slice(1)).slice(-20)
                      );
                    setEthAddress(ethAddress);
                  } else {
                    alert("Invalid Private Key");
                  }
                }
              }}
            >
              Save
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  } else {
    return (
      <AuthCodeScanner
        ethAddress={ethAddress}
        privkey={privKey}
        pubkey={pubKey}
      />
    );
  }
}

interface AuthCodeScannerProps {
  privkey: String;
  pubkey: String;
  ethAddress: String;
}

function AuthCodeScanner({ pubkey, ethAddress }: AuthCodeScannerProps) {
  const [data, setData] = useState<string | undefined>(undefined);
  const [_error, setError] = useState("");
  const [showVerifiedDialog, setShowVerifiedDialog] = useState(false);
  const [showDeniedDialog, setShowDeniedDialog] = useState(false);
  const [blocked, setBlocked] = useState(false);

  useEffect(() => {
    if (data && !showDeniedDialog && !showVerifiedDialog && !blocked) {
      setBlocked(true);
      let local = JSON.parse(data);
      local.key = pubkey;
      local.walletAddress = ethAddress;

      let payload = JSON.stringify(local);

      const mint = async () => {
        const resp = await fetch("https://api.styr.network/api/verify", {
          method: "POST",
          body: payload,
          headers: {
            "Content-Type": "application/json",
          },
        });
        if (resp.status == 200) {
          setShowVerifiedDialog(true);
        } else {
          setShowDeniedDialog(true);
        }
      };
      mint();
    }
  }, [data]);

  return (
    <>
      <Dialog open={showDeniedDialog}>
        <DialogContent>
          <div className="flex justify-center">
            <Ban size={90} color="red" />
          </div>
          <DialogFooter>
            <Button
              onClick={() => {
                setData("");
                setError("");
                setShowDeniedDialog(false);
              }}
            >
              Cool!
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      <Dialog open={showVerifiedDialog}>
        <DialogContent>
          <div className="flex justify-center">
            <Check size={90} color="green" />
          </div>
          <DialogFooter>
            <Button
              onClick={() => {
                setData("");
                setError("");
                setShowVerifiedDialog(false);
              }}
            >
              Cool!
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      {!(showDeniedDialog || showVerifiedDialog) ? (
        <Scanner
          options={{
            // delayBetweenScanAttempts: 1000,
            delayBetweenScanSuccess: 2000,
          }}
          components={{
            torch: false,
          }}
          onResult={(text, _) => setData(text)}
          onError={(error: any) => setError(error?.message)}
          styles={{
            container: {
              width: "100vw",
              margin: "auto",
            },
          }}
        />
      ) : null}
    </>
  );
}
