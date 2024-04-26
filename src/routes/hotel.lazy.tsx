import { cn } from "@/lib/utils";
import { createLazyFileRoute } from "@tanstack/react-router";
import { ReactNode, useEffect, useState } from "react";
import QRCode from "react-qr-code";
import useWebSocket from "react-use-websocket";

export const Route = createLazyFileRoute("/hotel")({
  component: () => Index(),
});

function Index() {
  const { lastMessage } = useWebSocket("wss://api.styr.network/ws");

  const [code, setCode] = useState<string>("");
  const [data, setData] = useState<string | undefined>(undefined);
  useEffect(() => {
    if (lastMessage) {
      let data = JSON.parse(lastMessage.data);
      setData(lastMessage.data);
      setCode(data.code);
      console.log(data);
    }
  }, [lastMessage]);
  if (code === "") {
    return <></>;
  }

  return <CodeCard code={code} data={data} />;
}

interface CodeCardProps {
  code: String;
  data?: String;
}
function CodeCard({ code, data }: CodeCardProps) {
  if (!data) {
    data = code;
  }
  return (
    <Card>
      <div className="flex flex-col items-center m-10 text-center">
        <h3>Tell code to traveller</h3>
        <h1 className="font-black mx-20 text-5xl text-center">
          {code.toUpperCase()}
        </h1>
        <h3 className="mt-10">or let them scan the code</h3>
        <div className="m-5 bg-white">
          <QRCode
            className="m-5 bg-white"
            level="H"
            value={data.toString()}
          ></QRCode>
        </div>
      </div>
    </Card>
  );
}

interface CardProps {
  children: ReactNode;
  className?: string;
}

function Card({ children, className }: CardProps) {
  return (
    <div
      className={cn(
        className,
        "max-w-sm rounded overflow-hidden bg-zinc-700 flex flex-col items-center"
      )}
    >
      {children}
    </div>
  );
}
