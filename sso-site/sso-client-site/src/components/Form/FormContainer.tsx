import { PropsWithChildren } from "react";
import {
  DefaultValues,
  FieldValues,
  SubmitErrorHandler,
  SubmitHandler,
  useForm as useRHF,
  UseFormProps,
  FormProvider,
  UseFormReturn,
} from "react-hook-form";
import { ZodSchema } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

type FormProps<T extends FieldValues> = {
  schema: ZodSchema<T>;
  title?: string;
  onSubmit: SubmitHandler<T>;
  onError?: SubmitErrorHandler<T>;
  submitButtonText?: string;
  values?: UseFormProps<T>["values"];
  defaultValues?: DefaultValues<T>;
  className?: string;
} & PropsWithChildren;

export type FormContext<T extends FieldValues> = {
  handleReset: () => void;
} & UseFormReturn<T>;

export const FormContainer = <T extends FieldValues>({
  defaultValues,
  className,
  onSubmit,
  onError,
  schema,
  children,
}: FormProps<T>) => {
  const form = useRHF<T>({
    defaultValues,
    resolver: zodResolver(schema),
  });
  const handleReset = () => {
    form.reset(defaultValues);
  };

  const extendedForm: FormContext<T> = {
    ...form,
    handleReset,
  };

  return (
    <FormProvider {...extendedForm}>
      <form className={className} onSubmit={extendedForm.handleSubmit(onSubmit, onError)}>{children}</form>
    </FormProvider>
  );
};
