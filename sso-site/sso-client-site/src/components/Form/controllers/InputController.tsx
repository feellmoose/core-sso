import { forwardRef, type ReactElement, type Ref } from "react";
import {
  Controller,
  type FieldValues,
  type Path,
  type ControllerRenderProps,
} from "react-hook-form";
import { useFormContext } from "@/hooks/useFormContext";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

export type InputControllerProps<T extends FieldValues> = Omit<
  React.ComponentProps<"input">,
  "name"
> & {
    name: Path<T>;
  errorClassName?: string;
  rules?: Parameters<typeof Controller>["0"]["rules"];
};

const InputControllerInner = <T extends FieldValues>(
  props: InputControllerProps<T>,
  ref: Ref<HTMLInputElement>
): ReactElement => {
  const { name, className, errorClassName, placeholder, ...restProps } = props;
  const {
    control,
    formState: { errors },
  } = useFormContext();
  const error = errors[name];

  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <div className="relative">
          <Input
            {...fieldAdapter(field)}
            {...restProps}
            ref={ref}
            placeholder={placeholder}
            className={cn(className, error && errorClassName)}
          />
          {error?.message && (
            <div className="text-red-500 text-sm mt-1">{error.message}</div>
          )}
        </div>
      )}
    />
  );
};

const fieldAdapter = <T extends FieldValues>(
  field: ControllerRenderProps<T>
) => ({
  ...field,
  value: field.value as React.ComponentProps<"input">["value"],
});

export const InputController = forwardRef(InputControllerInner) as <
  T extends FieldValues
>(
  props: InputControllerProps<T> & { ref?: Ref<HTMLInputElement> }
) => ReactElement;
