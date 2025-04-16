import { PropsWithChildren, useEffect, useRef } from "react";
import { cn } from "@/lib/utils";

interface RevealProps {
    className?: string
}
export const Reveal: React.FC<RevealProps & PropsWithChildren> = ({ className, children }) => {
    
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const observer = new IntersectionObserver(([entry]) => {
            if (entry.isIntersecting && ref.current) {
                ref.current.classList.add('visible')
            }
        }, { threshold: 0.2, rootMargin: '0px 0px -50px 0px' })
        if (ref.current) observer.observe(ref.current)
        
        return () => {
            observer.disconnect()
        }
    }, [])

    return (
        <div ref={ref} className={cn('reveal', className) }>
            { children }
        </div>
    )
}
