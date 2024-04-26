import { Button } from "@/components/ui/button";
import { createLazyFileRoute } from "@tanstack/react-router";
import { createRef, useState } from "react";
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
              onClick={() => {
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
  const [_data, setData] = useState("Please scan sth :3");
  const [_error, setError] = useState("");
  const [showVerifiedDialog, setShowVerifiedDialog] = useState(false);
  const [showDeniedDialog, setShowDeniedDialog] = useState(false);

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
            delayBetweenScanAttempts: 1000,
            delayBetweenScanSuccess: 5000,
          }}
          components={{
            torch: false,
          }}
          onResult={async (text, _result) => {
            console.log(text, showDeniedDialog, showVerifiedDialog);
            if (showVerifiedDialog || showDeniedDialog) {
              return;
            }
            setData(text);
            let data = JSON.parse(text);
            data.key = pubkey;
            data.ethAddress = ethAddress;

            console.log(data);
            try {
              let resp = await fetch("http://hercher.eu:8080/api/verify", {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                  "Content-Type": "application/json",
                },
              });
              if (resp.status == 200) {
                setShowVerifiedDialog(true);
              } else {
                setShowDeniedDialog(true);
              }
            } catch (e) {
              alert("an error occured");
            }
            // console.log(await resp.b());
          }}
          onError={(error: any) => setError(error?.message)}
          styles={{
            container: {
              width: "100vw",
              margin: "auto",
            },
          }}
        />
      ) : (
        <></>
      )}
    </>
  );
}
