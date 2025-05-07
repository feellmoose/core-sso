import { enqueueSnackbar, VariantType, OptionsObject } from "notistack";

type ShowSnackOptions = Partial<OptionsObject> & {
    variant?: VariantType;
    duration?: number;
  };
  
  export const showSnack = (message: string, options: ShowSnackOptions = {}) => {
    const defaultOptions: ShowSnackOptions = {
      variant: "success",
      duration: 3000,
    };
    return enqueueSnackbar(message, {
      ...defaultOptions,
      ...options,
    });
  };
  