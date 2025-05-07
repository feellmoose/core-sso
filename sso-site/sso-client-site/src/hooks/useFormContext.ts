import { FormContext } from "@/components/Form/FormContainer";
import { useFormContext as useRHFFormContext } from "react-hook-form";

const useFormContext = <T extends Record<string, unknown>>() => {
  const context = useRHFFormContext<T>() as FormContext<T>;

  if (context === undefined) {
    throw new Error("useFormContext must be used within a FormConatiner");
  }

  return context;
};

export { useFormContext };
