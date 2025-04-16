import { PropsWithChildren } from "react";
import { cn } from "@/lib/utils";

type BackgroundProps = React.HTMLProps<HTMLDivElement> & PropsWithChildren;
export const Background: React.FC<BackgroundProps> = ({
  className,
  children,
  ...props
}) => {
  return (
    <main>
      <div
        className={cn(
          "relative flex flex-col  h-[100vh] items-center justify-center bg-zinc-50 dark:bg-zinc-900  text-slate-950 transition-bg",
          className
        )}
        {...props}
      >
        <div className="absolute inset-0 overflow-hidden bg-gradient-to-b from-[rgb(235,237,244)] to-white">
          {children}
        </div>
      </div>
    </main>
  );
};
